package ctrlg.gyeongdodat.api.game_player.facade;

import ctrlg.gyeongdodat.api.game_player.dto.PlayerPositionResponse;
import ctrlg.gyeongdodat.domain.game_player.entity.GamePlayerPosRedis;
import ctrlg.gyeongdodat.domain.game_player.entity.GamePlayerRedis;
import ctrlg.gyeongdodat.domain.game_player.enums.PlayerStatus;
import ctrlg.gyeongdodat.domain.game_player.enums.Team;
import ctrlg.gyeongdodat.domain.game_player.service.GamePlayerPosRedisService;
import ctrlg.gyeongdodat.domain.game_player.service.GamePlayerRedisService;
import ctrlg.gyeongdodat.domain.game_player.service.command.GamePlayerCreateCommand;
import ctrlg.gyeongdodat.domain.game_player.service.command.GamePlayerPosCreateCommand;
import ctrlg.gyeongdodat.domain.game_player.service.command.GamePlayerPosUpdateCommand;
import ctrlg.gyeongdodat.domain.game_player.service.command.GamePlayerUpdateCommand;
import ctrlg.gyeongdodat.global.exception.ErrorCode;
import ctrlg.gyeongdodat.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GamePlayerFacade {

	private final GamePlayerRedisService playerService;
	private final GamePlayerPosRedisService playerPosService;

	/**
	 * 게임 참여: 플레이어 생성 + 초기 위치(0,0) 설정 + 도둑인 경우 번호 자동 부여
	 */
	public GamePlayerRedis joinGame(GamePlayerCreateCommand command) {
		GamePlayerRedis player = playerService.create(command);

		// 도둑인 경우 번호 자동 부여
		player = playerService.assignThiefNumber(player.getId());

		GamePlayerPosCreateCommand posCreate = GamePlayerPosCreateCommand.builder()
			.gamePlayerId(player.getId())
			.lat(BigDecimal.ZERO)
			.lng(BigDecimal.ZERO)
			.build();
		playerPosService.create(posCreate);

		return player;
	}

	public List<GamePlayerRedis> findPlayersByGame(String gameId) {
		return playerService.findByGameId(gameId);
	}

	public GamePlayerRedis findPlayerById(String id) {
		return playerService.findById(id);
	}

	public GamePlayerRedis updatePlayerStatus(String id, GamePlayerUpdateCommand command) {
		return playerService.update(id, command);
	}

	public GamePlayerPosRedis updatePlayerLocation(String id, GamePlayerPosUpdateCommand command) {
		return playerPosService.update(id, command);
	}

	/**
	 * 게임의 모든 플레이어 위치 조회
	 * @param gameId 게임 ID
	 * @return 모든 플레이어의 위치 정보 목록
	 */
	public List<PlayerPositionResponse> getAllPlayerPositions(String gameId) {
		List<GamePlayerRedis> players = playerService.findByGameId(gameId);

		return players.stream()
				.map(player -> {
					GamePlayerPosRedis pos = playerPosService.findByGamePlayerId(player.getId());
					return PlayerPositionResponse.builder()
							.playerId(player.getId())
							.team(player.getTeam())
							.status(player.getStatus())
							.lat(pos.getLat())
							.lng(pos.getLng())
							.thiefNumber(player.getThiefNumber())
							.build();
				})
				.collect(Collectors.toList());
	}

	/**
	 * 게임 나가기: 플레이어 정보 및 위치 정보 삭제
	 *
	 * @return 나간 플레이어가 속해있던 gameId (null이면 존재하지 않았던 플레이어)
	 */
	public String leaveGame(String gamePlayerId) {
		GamePlayerRedis player = playerService.findByIdOrNull(gamePlayerId);
		if (player == null) {
			return null;
		}

		String gameId = player.getGameId();

		// 위치 정보 삭제 시도
		try {
			playerPosService.delete(gamePlayerId);
		} catch (Exception e) {
			// 위치 정보가 이미 없을 수 있음
		}

		playerService.delete(gamePlayerId);
		return gameId;
	}

	/**
	 * 체포: 경찰이 도둑을 체포한다
	 * @param gameId 게임 ID
	 * @param policePlayerId 체포하는 경찰의 플레이어 ID
	 * @param thiefNumber 체포할 도둑의 번호
	 * @return 체포된 도둑 플레이어 정보
	 */
	public GamePlayerRedis arrestThief(String gameId, String policePlayerId, Integer thiefNumber) {
		// 경찰 권한 검증
		GamePlayerRedis police = playerService.findById(policePlayerId);
		if (police.getTeam() != Team.POLICE) {
			throw new GlobalException(ErrorCode.NOT_POLICE);
		}

		// 도둑 찾기
		GamePlayerRedis thief = playerService.findByGameIdAndThiefNumber(gameId, thiefNumber);
		if (thief == null) {
			throw new GlobalException(ErrorCode.THIEF_NOT_FOUND);
		}

		// 이미 수감 상태인지 확인
		if (thief.getStatus() == PlayerStatus.JAILED) {
			throw new GlobalException(ErrorCode.ALREADY_JAILED);
		}

		// 도둑 상태를 JAILED로 변경
		GamePlayerUpdateCommand thiefUpdate = GamePlayerUpdateCommand.builder()
				.status(PlayerStatus.JAILED)
				.build();
		GamePlayerRedis updatedThief = playerService.update(thief.getId(), thiefUpdate);

		// 경찰 caughtCount 증가
		GamePlayerUpdateCommand policeUpdate = GamePlayerUpdateCommand.builder()
				.caughtCount(police.getCaughtCount() + 1)
				.build();
		playerService.update(policePlayerId, policeUpdate);

		return updatedThief;
	}

	/**
	 * 구출: 도둑이 수감된 동료를 구출한다
	 * @param gameId 게임 ID
	 * @param rescuerPlayerId 구출하는 도둑의 플레이어 ID
	 * @param targetThiefNumber 구출할 도둑의 번호
	 * @return 구출된 도둑 플레이어 정보
	 */
	public GamePlayerRedis rescueThief(String gameId, String rescuerPlayerId, Integer targetThiefNumber) {
		// 구출자 권한 검증 (도둑이어야 함)
		GamePlayerRedis rescuer = playerService.findById(rescuerPlayerId);
		if (rescuer.getTeam() != Team.THIEF) {
			throw new GlobalException(ErrorCode.NOT_THIEF);
		}

		// 구출자 상태 검증 (수감 상태가 아니어야 함)
		if (rescuer.getStatus() == PlayerStatus.JAILED) {
			throw new GlobalException(ErrorCode.RESCUER_IS_JAILED);
		}

		// 대상 도둑 찾기
		GamePlayerRedis targetThief = playerService.findByGameIdAndThiefNumber(gameId, targetThiefNumber);
		if (targetThief == null) {
			throw new GlobalException(ErrorCode.THIEF_NOT_FOUND);
		}

		// 대상 상태 검증 (수감 상태여야 함)
		if (targetThief.getStatus() != PlayerStatus.JAILED) {
			throw new GlobalException(ErrorCode.NOT_JAILED);
		}

		// 대상 상태를 ACTIVE로 변경, jailedJailNo 초기화
		GamePlayerUpdateCommand targetUpdate = GamePlayerUpdateCommand.builder()
				.status(PlayerStatus.ACTIVE)
				.clearJailedJailNo(true)
				.build();
		GamePlayerRedis updatedTarget = playerService.update(targetThief.getId(), targetUpdate);

		// 구출자 escapeCount 증가
		GamePlayerUpdateCommand rescuerUpdate = GamePlayerUpdateCommand.builder()
				.escapeCount(rescuer.getEscapeCount() + 1)
				.build();
		playerService.update(rescuerPlayerId, rescuerUpdate);

		return updatedTarget;
	}
}
