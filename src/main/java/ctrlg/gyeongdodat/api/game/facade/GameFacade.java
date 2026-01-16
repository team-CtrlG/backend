package ctrlg.gyeongdodat.api.game.facade;

import ctrlg.gyeongdodat.domain.game.dto.Location;
import ctrlg.gyeongdodat.domain.game.entity.GameRedis;
import ctrlg.gyeongdodat.domain.game.enums.WinTeam;
import ctrlg.gyeongdodat.domain.game.service.GameRedisService;
import ctrlg.gyeongdodat.domain.game.service.command.GameCreateCommand;
import ctrlg.gyeongdodat.domain.game.service.command.GameUpdateCommand;
import ctrlg.gyeongdodat.domain.game_log.service.GameLogRedisService;
import ctrlg.gyeongdodat.domain.game_player.entity.GamePlayerRedis;
import ctrlg.gyeongdodat.domain.game_player.enums.PlayerStatus;
import ctrlg.gyeongdodat.domain.game_player.enums.Team;
import ctrlg.gyeongdodat.domain.game_player.service.GamePlayerRedisService;
import ctrlg.gyeongdodat.domain.game_player.service.command.GamePlayerUpdateCommand;
import ctrlg.gyeongdodat.global.exception.ErrorCode;
import ctrlg.gyeongdodat.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GameFacade {

	private final GameRedisService gameService;
	private final GamePlayerRedisService playerService;
	private final GameLogRedisService logService;

	public GameRedis createGame(GameCreateCommand command) {
		return gameService.create(command);
	}

	public List<GameRedis> findAllGames() {
		return gameService.findAll();
	}

	public GameRedis findGameById(String gameId) {
		return gameService.findById(gameId);
	}

	public GameRedis setGameArea(String gameId, List<Location> gameArea) {
		GameUpdateCommand update = GameUpdateCommand.builder()
				.gameArea(gameArea)
				.build();
		return gameService.update(gameId, update);
	}

	public GameRedis startGame(String gameId) {
		GameUpdateCommand update = GameUpdateCommand.builder()
			.startedAt(LocalDateTime.now())
			.build();
		return gameService.update(gameId, update);
	}

	/**
	 * 게임 삭제 시 관련 플레이어와 로그도 함께 정리
	 */
	public void deleteGame(String gameId) {
		gameService.delete(gameId);
		playerService.deleteByGameId(gameId);
		logService.deleteByGameId(gameId);
	}

	/**
	 * 출석 체크
	 * @return 출석 처리된 플레이어 정보
	 */
	public GamePlayerRedis checkAttendance(String gameId, String playerId, String code) {
		// 게임 조회 및 출석코드 검증
		GameRedis game = gameService.findById(gameId);
		if (!code.equals(game.getAttendanceCode())) {
			throw new GlobalException(ErrorCode.INVALID_ATTENDANCE_CODE);
		}

		// 플레이어 조회 및 이미 출석 여부 확인
		GamePlayerRedis player = playerService.findById(playerId);
		if (player.isAttendanceYn()) {
			throw new GlobalException(ErrorCode.ALREADY_ATTENDED);
		}

		// 출석 처리
		GamePlayerUpdateCommand updateCommand = GamePlayerUpdateCommand.builder()
				.attendanceYn(true)
				.build();
		return playerService.update(playerId, updateCommand);
	}

	/**
	 * 남은 게임 시간 조회 (초 단위)
	 * @param gameId 게임 ID
	 * @return 남은 시간 (초). 시작 전이면 전체 시간, 종료 후면 0
	 */
	public Integer getRemainingTimeSec(String gameId) {
		GameRedis game = gameService.findById(gameId);

		// 게임 종료된 경우
		if (game.getEndedAt() != null) {
			return 0;
		}

		// 게임 시작 전인 경우
		if (game.getStartedAt() == null) {
			return game.getGameTimeSec();
		}

		// 게임 진행 중인 경우: startedAt + gameTimeSec - now
		LocalDateTime endTime = game.getStartedAt().plusSeconds(game.getGameTimeSec());
		long remainingSec = Duration.between(LocalDateTime.now(), endTime).getSeconds();

		// 음수면 0 반환
		return Math.max(0, (int) remainingSec);
	}

	/**
	 * 게임 종료
	 * @param gameId 게임 ID
	 * @return 종료된 게임 정보 (승리 팀 포함)
	 */
	public GameRedis endGame(String gameId) {
		GameRedis game = gameService.findById(gameId);
		List<GamePlayerRedis> players = playerService.findByGameId(gameId);

		// 승리 팀 판정
		WinTeam winTeam = determineWinner(players);

		// 게임 종료 처리
		GameUpdateCommand update = GameUpdateCommand.builder()
				.endedAt(LocalDateTime.now())
				.winTeam(winTeam)
				.build();

		return gameService.update(gameId, update);
	}

	/**
	 * 승리 팀 판정
	 * - 모든 도둑 JAILED → POLICE 승리
	 * - 도둑 생존 → THIEF 승리
	 * - 도둑 없음 → DRAW
	 * @param players 게임 참가자 목록
	 * @return 승리 팀
	 */
	private WinTeam determineWinner(List<GamePlayerRedis> players) {
		List<GamePlayerRedis> thieves = players.stream()
				.filter(p -> p.getTeam() == Team.THIEF)
				.toList();

		// 도둑이 없으면 무승부
		if (thieves.isEmpty()) {
			return WinTeam.DRAW;
		}

		// 모든 도둑이 수감되면 경찰 승리
		boolean allThievesJailed = thieves.stream()
				.allMatch(t -> t.getStatus() == PlayerStatus.JAILED);
		if (allThievesJailed) {
			return WinTeam.POLICE;
		}

		// 도둑 생존 → 도둑 승리
		return WinTeam.THIEF;
	}
}
