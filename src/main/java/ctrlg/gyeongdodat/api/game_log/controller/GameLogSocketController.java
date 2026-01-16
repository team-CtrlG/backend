package ctrlg.gyeongdodat.api.game_log.controller;

import ctrlg.gyeongdodat.api.game_log.facade.GameLogFacade;
import ctrlg.gyeongdodat.domain.game_log.entity.GameLogRedis;
import ctrlg.gyeongdodat.domain.game_log.service.command.GameLogCreateCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GameLogSocketController {

	private final GameLogFacade logFacade;
	private final SimpMessagingTemplate messagingTemplate;

	/**
	 * 게임 로그 생성 (액션 발생 시)
	 * Client sends to: /app/game/log/create
	 * Broadcasts to: /send/game/{gameId}/log/created
	 */
	@MessageMapping("/game/log/create")
	public void createLog(@Payload GameLogCreateCommand command) {
		GameLogRedis logEntry = logFacade.createLog(command);
		messagingTemplate.convertAndSend("/send/game/" + command.getGameId() + "/log/created",
			logEntry);
	}

	/**
	 * 게임 로그 목록 조회
	 * Client sends to: /app/game/{gameId}/logs
	 * Broadcasts to: /send/game/{gameId}/logs
	 */
	@MessageMapping("/game/{gameId}/logs")
	public void findLogsByGame(@DestinationVariable String gameId) {
		List<GameLogRedis> logs = logFacade.findLogsByGame(gameId);
		messagingTemplate.convertAndSend("/send/game/" + gameId + "/logs", logs);
	}
}
