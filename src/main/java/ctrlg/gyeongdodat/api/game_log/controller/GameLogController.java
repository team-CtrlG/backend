package ctrlg.gyeongdodat.api.game_log.controller;

import ctrlg.gyeongdodat.api.game_log.facade.GameLogFacade;
import ctrlg.gyeongdodat.domain.game_log.entity.GameLogRedis;
import ctrlg.gyeongdodat.domain.game_log.service.command.GameLogCreateCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/games/{gameId}/logs")
public class GameLogController implements GameLogControllerSpec {

	private final GameLogFacade logFacade;

	/**
	 * 게임 로그 생성
	 */
	@PostMapping
	public ResponseEntity<GameLogRedis> createLog(
			@PathVariable String gameId,
			@RequestBody GameLogCreateCommand command) {
		GameLogRedis logEntry = logFacade.createLog(command);
		return ResponseEntity.ok(logEntry);
	}

	/**
	 * 게임 로그 조회
	 */
	@GetMapping
	public ResponseEntity<List<GameLogRedis>> findLogsByGame(@PathVariable String gameId) {
		List<GameLogRedis> logs = logFacade.findLogsByGame(gameId);
		return ResponseEntity.ok(logs);
	}
}
