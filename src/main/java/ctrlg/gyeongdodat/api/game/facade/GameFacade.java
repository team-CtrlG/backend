package ctrlg.gyeongdodat.api.game.facade;

import ctrlg.gyeongdodat.domain.game.dto.Location;
import ctrlg.gyeongdodat.domain.game.entity.GameRedis;
import ctrlg.gyeongdodat.domain.game.service.GameRedisService;
import ctrlg.gyeongdodat.domain.game.service.command.GameCreateCommand;
import ctrlg.gyeongdodat.domain.game.service.command.GameUpdateCommand;
import ctrlg.gyeongdodat.domain.game_log.service.GameLogRedisService;
import ctrlg.gyeongdodat.domain.game_player.service.GamePlayerRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
}
