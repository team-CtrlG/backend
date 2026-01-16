package ctrlg.gyeongdodat.api.game_log.facade;

import ctrlg.gyeongdodat.domain.game_log.entity.GameLogRedis;
import ctrlg.gyeongdodat.domain.game_log.service.GameLogRedisService;
import ctrlg.gyeongdodat.domain.game_log.service.command.GameLogCreateCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GameLogFacade {

	private final GameLogRedisService logService;

	public GameLogRedis createLog(GameLogCreateCommand command) {
		return logService.create(command);
	}

	public List<GameLogRedis> findLogsByGame(String gameId) {
		return logService.findByGameId(gameId);
	}
}
