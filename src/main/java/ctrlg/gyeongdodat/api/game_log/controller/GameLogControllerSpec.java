package ctrlg.gyeongdodat.api.game_log.controller;

import ctrlg.gyeongdodat.domain.game_log.entity.GameLogRedis;
import ctrlg.gyeongdodat.domain.game_log.service.command.GameLogCreateCommand;
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

@Tag(name = "GameLog", description = "게임 로그 API")
public interface GameLogControllerSpec {

	@Operation(summary = "게임 로그 생성", description = "게임 진행 중 발생한 이벤트 로그를 생성합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "로그 생성 성공",
					content = @Content(schema = @Schema(implementation = GameLogRedis.class)))
	})
	@PostMapping
	ResponseEntity<GameLogRedis> createLog(
			@Parameter(description = "게임 ID") @PathVariable String gameId,
			@RequestBody GameLogCreateCommand command);

	@Operation(summary = "게임 로그 조회", description = "특정 게임의 모든 로그를 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "로그 조회 성공")
	})
	@GetMapping
	ResponseEntity<List<GameLogRedis>> findLogsByGame(
			@Parameter(description = "게임 ID") @PathVariable String gameId);
}
