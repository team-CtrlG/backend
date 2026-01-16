package ctrlg.gyeongdodat.api.game.controller;

import ctrlg.gyeongdodat.api.game.dto.AttendanceRequest;
import ctrlg.gyeongdodat.api.game.facade.GameFacade;
import ctrlg.gyeongdodat.domain.game.dto.SetGameAreaRequest;
import ctrlg.gyeongdodat.domain.game.entity.GameRedis;
import ctrlg.gyeongdodat.domain.game.service.command.GameCreateCommand;
import ctrlg.gyeongdodat.domain.game_player.entity.GamePlayerRedis;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/games")
public class GameController implements GameControllerSpec {

	private final GameFacade gameFacade;

	/**
	 * 게임 생성
	 */
	@PostMapping
	public ResponseEntity<GameRedis> createGame(@RequestBody GameCreateCommand command) {
		log.info("게임 생성 요청 - 게임시간: {}초", command.getGameTimeSec());
		GameRedis game = gameFacade.createGame(command);
		return ResponseEntity.ok(game);
	}

	/**
	 * 전체 게임 목록 조회
	 */
	@GetMapping
	public ResponseEntity<List<GameRedis>> findAllGames() {
		List<GameRedis> games = gameFacade.findAllGames();
		return ResponseEntity.ok(games);
	}

	/**
	 * 게임 정보 조회
	 */
	@GetMapping("/{gameId}")
	public ResponseEntity<GameRedis> findGame(@PathVariable String gameId) {
		GameRedis game = gameFacade.findGameById(gameId);
		return ResponseEntity.ok(game);
	}

	/**
	 * 게임 활동 범위 지정
	 */
	@PutMapping("/{gameId}/area")
	public ResponseEntity<GameRedis> setGameArea(
			@PathVariable String gameId,
			@RequestBody SetGameAreaRequest request) {
		log.info("게임 영역 설정 요청 - 게임ID: {}", gameId);
		GameRedis game = gameFacade.setGameArea(gameId, request.getGameArea());
		return ResponseEntity.ok(game);
	}

	/**
	 * 게임 시작
	 */
	@PostMapping("/{gameId}/start")
	public ResponseEntity<GameRedis> startGame(@PathVariable String gameId) {
		log.info("게임 시작 요청 - 게임ID: {}", gameId);
		GameRedis game = gameFacade.startGame(gameId);
		return ResponseEntity.ok(game);
	}

	/**
	 * 게임 삭제 (플레이어 및 로그 포함)
	 */
	@DeleteMapping("/{gameId}")
	public ResponseEntity<Void> deleteGame(@PathVariable String gameId) {
		log.info("게임 삭제 요청 - 게임ID: {}", gameId);
		gameFacade.deleteGame(gameId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * 출석 체크
	 */
	@PostMapping("/{gameId}/attendance")
	public ResponseEntity<GamePlayerRedis> checkAttendance(
			@PathVariable String gameId,
			@RequestBody AttendanceRequest request) {
		log.info("출석 체크 요청 - 플레이어ID: {}, 게임ID: {}", request.getPlayerId(), gameId);
		GamePlayerRedis player = gameFacade.checkAttendance(gameId, request.getPlayerId(), request.getCode());
		return ResponseEntity.ok(player);
	}

	/**
	 * 남은 시간 조회
	 */
	@GetMapping("/{gameId}/time")
	public ResponseEntity<Integer> getRemainingTime(@PathVariable String gameId) {
		log.info("남은 시간 조회 요청 - 게임ID: {}", gameId);
		Integer remainingSec = gameFacade.getRemainingTimeSec(gameId);
		return ResponseEntity.ok(remainingSec);
	}

	/**
	 * 게임 종료 및 승리 팀 판정
	 */
	@PostMapping("/{gameId}/end")
	public ResponseEntity<GameRedis> endGame(@PathVariable String gameId) {
		log.info("게임 종료 요청 - 게임ID: {}", gameId);
		GameRedis game = gameFacade.endGame(gameId);
		return ResponseEntity.ok(game);
	}
}
