package ctrlg.gyeongdodat.api.game_player.controller;

import ctrlg.gyeongdodat.api.game_player.dto.ArrestRequest;
import ctrlg.gyeongdodat.api.game_player.dto.RescueRequest;
import ctrlg.gyeongdodat.domain.game_player.entity.GamePlayerRedis;
import ctrlg.gyeongdodat.domain.game_player.service.command.GamePlayerCreateCommand;
import ctrlg.gyeongdodat.domain.game_player.service.command.GamePlayerUpdateCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "GamePlayer", description = "게임 플레이어 관리 API")
public interface GamePlayerControllerSpec {

	@Operation(summary = "게임 참여", description = "플레이어가 게임에 참여합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "게임 참여 성공",
					content = @Content(schema = @Schema(implementation = GamePlayerRedis.class)))
	})
	@PostMapping("/games/{gameId}/players")
	ResponseEntity<GamePlayerRedis> joinGame(
			@Parameter(description = "게임 ID") @PathVariable String gameId,
			@RequestBody GamePlayerCreateCommand command);

	@Operation(summary = "플레이어 목록 조회", description = "게임에 참여한 플레이어 목록을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "플레이어 목록 조회 성공")
	})
	@GetMapping("/games/{gameId}/players")
	ResponseEntity<List<GamePlayerRedis>> findPlayersByGame(
			@Parameter(description = "게임 ID") @PathVariable String gameId);

	@Operation(summary = "플레이어 상태 업데이트", description = "플레이어의 상태를 업데이트합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "플레이어 상태 업데이트 성공",
					content = @Content(schema = @Schema(implementation = GamePlayerRedis.class)))
	})
	@PutMapping("/players/{playerId}")
	ResponseEntity<GamePlayerRedis> updatePlayer(
			@Parameter(description = "플레이어 ID") @PathVariable String playerId,
			@RequestBody GamePlayerUpdateCommand command);

	@Operation(summary = "게임 나가기", description = "플레이어가 게임에서 나갑니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "게임 나가기 성공")
	})
	@DeleteMapping("/players/{playerId}")
	ResponseEntity<Void> leaveGame(
			@Parameter(description = "플레이어 ID") @PathVariable String playerId);

	@Operation(summary = "체포", description = "경찰이 도둑을 체포합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "체포 성공",
					content = @Content(schema = @Schema(implementation = GamePlayerRedis.class)))
	})
	@PostMapping("/games/{gameId}/arrest")
	ResponseEntity<GamePlayerRedis> arrestThief(
			@Parameter(description = "게임 ID") @PathVariable String gameId,
			@RequestBody ArrestRequest request);

	@Operation(summary = "구출", description = "도둑이 수감된 도둑을 구출합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "구출 성공",
					content = @Content(schema = @Schema(implementation = GamePlayerRedis.class)))
	})
	@PostMapping("/games/{gameId}/rescue")
	ResponseEntity<GamePlayerRedis> rescueThief(
			@Parameter(description = "게임 ID") @PathVariable String gameId,
			@RequestBody RescueRequest request);
}
