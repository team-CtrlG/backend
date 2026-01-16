package ctrlg.gyeongdodat.api.game_player.controller;

import ctrlg.gyeongdodat.api.game_player.dto.ArrestRequest;
import ctrlg.gyeongdodat.api.game_player.dto.RescueRequest;
import ctrlg.gyeongdodat.api.game_player.facade.GamePlayerFacade;
import ctrlg.gyeongdodat.domain.game_player.entity.GamePlayerRedis;
import ctrlg.gyeongdodat.domain.game_player.service.command.GamePlayerCreateCommand;
import ctrlg.gyeongdodat.domain.game_player.service.command.GamePlayerUpdateCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class GamePlayerController implements GamePlayerControllerSpec {

	private final GamePlayerFacade gamePlayerFacade;

	/**
	 * 게임 참여
	 */
	@PostMapping("/games/{gameId}/players")
	public ResponseEntity<GamePlayerRedis> joinGame(
			@PathVariable String gameId,
			@RequestBody GamePlayerCreateCommand command) {
		log.info("게임 참가 요청 - 유저ID: {}, 게임ID: {}", command.getUserId(), gameId);
		GamePlayerRedis player = gamePlayerFacade.joinGame(command);
		return ResponseEntity.ok(player);
	}

	/**
	 * 플레이어 목록 조회
	 */
	@GetMapping("/games/{gameId}/players")
	public ResponseEntity<List<GamePlayerRedis>> findPlayersByGame(@PathVariable String gameId) {
		List<GamePlayerRedis> players = gamePlayerFacade.findPlayersByGame(gameId);
		return ResponseEntity.ok(players);
	}

	/**
	 * 플레이어 상태 업데이트
	 */
	@PutMapping("/players/{playerId}")
	public ResponseEntity<GamePlayerRedis> updatePlayer(
			@PathVariable String playerId,
			@RequestBody GamePlayerUpdateCommand command) {
		GamePlayerRedis player = gamePlayerFacade.updatePlayerStatus(playerId, command);
		return ResponseEntity.ok(player);
	}

	/**
	 * 게임 나가기
	 */
	@DeleteMapping("/players/{playerId}")
	public ResponseEntity<Void> leaveGame(@PathVariable String playerId) {
		gamePlayerFacade.leaveGame(playerId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * 체포 (경찰 → 도둑)
	 */
	@PostMapping("/games/{gameId}/arrest")
	public ResponseEntity<GamePlayerRedis> arrestThief(
			@PathVariable String gameId,
			@RequestBody ArrestRequest request) {
		log.info("체포 요청 - 경찰ID: {}, 도둑번호: {}, 게임ID: {}", request.getPolicePlayerId(), request.getThiefNumber(), gameId);
		GamePlayerRedis arrestedThief = gamePlayerFacade.arrestThief(gameId, request.getPolicePlayerId(), request.getThiefNumber());
		return ResponseEntity.ok(arrestedThief);
	}

	/**
	 * 구출 (도둑 → 수감자)
	 */
	@PostMapping("/games/{gameId}/rescue")
	public ResponseEntity<GamePlayerRedis> rescueThief(
			@PathVariable String gameId,
			@RequestBody RescueRequest request) {
		log.info("구출 요청 - 구출자ID: {}, 대상도둑번호: {}, 게임ID: {}", request.getRescuerPlayerId(), request.getTargetThiefNumber(), gameId);
		GamePlayerRedis rescuedThief = gamePlayerFacade.rescueThief(gameId, request.getRescuerPlayerId(), request.getTargetThiefNumber());
		return ResponseEntity.ok(rescuedThief);
	}
}
