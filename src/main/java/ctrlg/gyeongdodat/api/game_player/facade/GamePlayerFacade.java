package ctrlg.gyeongdodat.api.game_player.facade;

import ctrlg.gyeongdodat.domain.game_player.entity.GamePlayerPosRedis;
import ctrlg.gyeongdodat.domain.game_player.entity.GamePlayerRedis;
import ctrlg.gyeongdodat.domain.game_player.service.GamePlayerPosRedisService;
import ctrlg.gyeongdodat.domain.game_player.service.GamePlayerRedisService;
import ctrlg.gyeongdodat.domain.game_player.service.command.GamePlayerCreateCommand;
import ctrlg.gyeongdodat.domain.game_player.service.command.GamePlayerPosCreateCommand;
import ctrlg.gyeongdodat.domain.game_player.service.command.GamePlayerPosUpdateCommand;
import ctrlg.gyeongdodat.domain.game_player.service.command.GamePlayerUpdateCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GamePlayerFacade {

	private final GamePlayerRedisService playerService;
	private final GamePlayerPosRedisService playerPosService;

	/**
	 * 게임 참여: 플레이어 생성 + 초기 위치(0,0) 설정
	 */
	public GamePlayerRedis joinGame(GamePlayerCreateCommand command) {
		GamePlayerRedis player = playerService.create(command);

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
}
