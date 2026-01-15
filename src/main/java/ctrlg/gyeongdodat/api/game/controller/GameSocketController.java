package ctrlg.gyeongdodat.api.game.controller;

import ctrlg.gyeongdodat.api.game.facade.GameFacade;
import ctrlg.gyeongdodat.domain.game.entity.GameRedis;
import ctrlg.gyeongdodat.domain.game.service.command.GameCreateCommand;
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

public class GameSocketController {

	private final GameFacade gameFacade;
	private final SimpMessagingTemplate messagingTemplate;

	/**
	 * 게임 생성
     * Client sends to: /app/game/create
     * Broadcasts to: /send/game/created
	 */
	@MessageMapping("/game/create")
	public void createGame(@Payload GameCreateCommand command) {
		log.info("Creating game with time: {}", command.getGameTimeSec());
		GameRedis game = gameFacade.createGame(command);
		messagingTemplate.convertAndSend("/send/game/created", game);
	}

	/**
	 * 전체 게임 목록 조회
     * Client sends to: /app/game/list
     * Broadcasts to: /send/game/list
	 */
	@MessageMapping("/game/list")
	public void findAllGames() {
		List<GameRedis> games = gameFacade.findAllGames();
		messagingTemplate.convertAndSend("/send/game/list", games);
	}

	/**
	 * 특정 게임 정보 조회
     * Client sends to: /app/game/{gameId}/info
     * Broadcasts to: /send/game/{gameId}/info
	 */
	@MessageMapping("/game/{gameId}/info")
	public void findGame(@DestinationVariable String gameId) {
		GameRedis game = gameFacade.findGameById(gameId);
		messagingTemplate.convertAndSend("/send/game/" + gameId + "/info", game);
	}

	/**
	 * 게임 시작
	 * Client sends to: /app/game/{gameId}/start
	 * Broadcasts to: /send/game/{gameId}/start
	 */
	@MessageMapping("/game/{gameId}/start")
	public void startGame(@DestinationVariable String gameId) {
		log.info("Starting game {}", gameId);
		GameRedis game = gameFacade.startGame(gameId);
		messagingTemplate.convertAndSend("/send/game/" + gameId + "/start", game);
	}

	/**
	 * 게임 삭제 (플레이어 및 로그 포함)
	 * Client sends to: /app/game/{gameId}/delete
	 * Broadcasts to: /send/game/deleted (Deleted ID)
	 */
	@MessageMapping("/game/{gameId}/delete")
	public void deleteGame(@DestinationVariable String gameId) {
		log.info("Deleting game {}", gameId);
		gameFacade.deleteGame(gameId);
		messagingTemplate.convertAndSend("/send/game/deleted", gameId);
	}
}
