package ctrlg.gyeongdodat.api.game.controller;

import ctrlg.gyeongdodat.api.game.dto.AttendanceRequest;
import ctrlg.gyeongdodat.domain.game.dto.SetGameAreaRequest;
import ctrlg.gyeongdodat.domain.game.entity.GameRedis;
import ctrlg.gyeongdodat.domain.game.service.command.GameCreateCommand;
import ctrlg.gyeongdodat.domain.game_player.entity.GamePlayerRedis;
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

@Tag(name = "Game", description = "게임 관리 API")
public interface GameControllerSpec {

	@Operation(summary = "게임 생성", description = "새로운 게임을 생성합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "게임 생성 성공",
					content = @Content(schema = @Schema(implementation = GameRedis.class)))
	})
	@PostMapping
	ResponseEntity<GameRedis> createGame(
			@RequestBody GameCreateCommand command);

	@Operation(summary = "전체 게임 목록 조회", description = "모든 게임 목록을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "게임 목록 조회 성공")
	})
	@GetMapping
	ResponseEntity<List<GameRedis>> findAllGames();

	@Operation(summary = "게임 정보 조회", description = "특정 게임의 정보를 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "게임 정보 조회 성공",
					content = @Content(schema = @Schema(implementation = GameRedis.class)))
	})
	@GetMapping("/{gameId}")
	ResponseEntity<GameRedis> findGame(
			@Parameter(description = "게임 ID") @PathVariable String gameId);

	@Operation(summary = "게임 활동 범위 지정", description = "게임의 활동 가능 영역을 설정합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "게임 영역 설정 성공",
					content = @Content(schema = @Schema(implementation = GameRedis.class)))
	})
	@PutMapping("/{gameId}/area")
	ResponseEntity<GameRedis> setGameArea(
			@Parameter(description = "게임 ID") @PathVariable String gameId,
			@RequestBody SetGameAreaRequest request);

	@Operation(summary = "게임 시작", description = "게임을 시작합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "게임 시작 성공",
					content = @Content(schema = @Schema(implementation = GameRedis.class)))
	})
	@PostMapping("/{gameId}/start")
	ResponseEntity<GameRedis> startGame(
			@Parameter(description = "게임 ID") @PathVariable String gameId);

	@Operation(summary = "게임 삭제", description = "게임과 관련된 플레이어, 로그를 모두 삭제합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "게임 삭제 성공")
	})
	@DeleteMapping("/{gameId}")
	ResponseEntity<Void> deleteGame(
			@Parameter(description = "게임 ID") @PathVariable String gameId);

	@Operation(summary = "출석 체크", description = "플레이어의 출석을 확인합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "출석 체크 성공",
					content = @Content(schema = @Schema(implementation = GamePlayerRedis.class)))
	})
	@PostMapping("/{gameId}/attendance")
	ResponseEntity<GamePlayerRedis> checkAttendance(
			@Parameter(description = "게임 ID") @PathVariable String gameId,
			@RequestBody AttendanceRequest request);

	@Operation(summary = "남은 시간 조회", description = "게임의 남은 시간을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "남은 시간 조회 성공",
					content = @Content(schema = @Schema(implementation = Integer.class)))
	})
	@GetMapping("/{gameId}/time")
	ResponseEntity<Integer> getRemainingTime(
			@Parameter(description = "게임 ID") @PathVariable String gameId);

	@Operation(summary = "게임 종료", description = "게임을 종료하고 승리 팀을 판정합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "게임 종료 성공",
					content = @Content(schema = @Schema(implementation = GameRedis.class)))
	})
	@PostMapping("/{gameId}/end")
	ResponseEntity<GameRedis> endGame(
			@Parameter(description = "게임 ID") @PathVariable String gameId);
}
