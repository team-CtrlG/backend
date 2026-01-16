package ctrlg.gyeongdodat.api.game.controller;

import ctrlg.gyeongdodat.api.game.dto.AttendanceRequest;
import ctrlg.gyeongdodat.api.game.facade.GameFacade;
import ctrlg.gyeongdodat.domain.game.dto.SetGameAreaRequest;
import ctrlg.gyeongdodat.domain.game.entity.GameRedis;
import ctrlg.gyeongdodat.domain.game.service.command.GameCreateCommand;
import ctrlg.gyeongdodat.domain.game_player.entity.GamePlayerRedis;
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
		log.info("게임 생성 요청 - 게임시간: {}초", command.getGameTimeSec());
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
	 * 게임 활동 범위 지정
	 * Client sends to: /app/game/{gameId}/set-area
	 * Broadcasts to: /send/game/{gameId}/area-set
	 */
	@MessageMapping("/game/{gameId}/set-area")
	public void setGameArea(@DestinationVariable String gameId, @Payload SetGameAreaRequest request) {
		log.info("게임 영역 설정 요청 - 게임ID: {}", gameId);
		GameRedis game = gameFacade.setGameArea(gameId, request.getGameArea());
		messagingTemplate.convertAndSend("/send/game/" + gameId + "/area-set", game);
	}

	/**
	 * 게임 시작
	 * Client sends to: /app/game/{gameId}/start
	 * Broadcasts to: /send/game/{gameId}/start
	 */
	@MessageMapping("/game/{gameId}/start")
	public void startGame(@DestinationVariable String gameId) {
		log.info("게임 시작 요청 - 게임ID: {}", gameId);
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
		log.info("게임 삭제 요청 - 게임ID: {}", gameId);
		gameFacade.deleteGame(gameId);
		messagingTemplate.convertAndSend("/send/game/deleted", gameId);
	}

	/**
	 * 출석 체크
	 * Client sends to: /app/game/{gameId}/attendance
	 * Broadcasts to: /send/game/{gameId}/attendance
	 */
	@MessageMapping("/game/{gameId}/attendance")
	public void checkAttendance(@DestinationVariable String gameId, @Payload AttendanceRequest request) {
		log.info("출석 체크 요청 - 플레이어ID: {}, 게임ID: {}", request.getPlayerId(), gameId);
		GamePlayerRedis player = gameFacade.checkAttendance(gameId, request.getPlayerId(), request.getCode());
		messagingTemplate.convertAndSend("/send/game/" + gameId + "/attendance", player);
	}

	/**
	 * 남은 시간 조회
	 * Client sends to: /app/game/{gameId}/time
	 * Broadcasts to: /send/game/{gameId}/time
	 */
	@MessageMapping("/game/{gameId}/time")
	public void getRemainingTime(@DestinationVariable String gameId) {
		log.info("남은 시간 조회 요청 - 게임ID: {}", gameId);
		Integer remainingSec = gameFacade.getRemainingTimeSec(gameId);
		messagingTemplate.convertAndSend("/send/game/" + gameId + "/time", remainingSec);
	}

	/**
	 * 게임 종료 및 승리 팀 판정
	 * Client sends to: /app/game/{gameId}/end
	 * Broadcasts to: /send/game/{gameId}/end
	 */
	@MessageMapping("/game/{gameId}/end")
	public void endGame(@DestinationVariable String gameId) {
		log.info("게임 종료 요청 - 게임ID: {}", gameId);
		GameRedis game = gameFacade.endGame(gameId);
		messagingTemplate.convertAndSend("/send/game/" + gameId + "/end", game);
	}
}
