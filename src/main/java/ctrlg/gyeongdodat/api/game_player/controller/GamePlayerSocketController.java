package ctrlg.gyeongdodat.api.game_player.controller;

import ctrlg.gyeongdodat.api.game_player.dto.PlayerPositionResponse;
import ctrlg.gyeongdodat.api.game_player.facade.GamePlayerFacade;
import ctrlg.gyeongdodat.domain.game_player.entity.GamePlayerPosRedis;
import ctrlg.gyeongdodat.domain.game_player.entity.GamePlayerRedis;
import ctrlg.gyeongdodat.domain.game_player.service.command.GamePlayerPosUpdateCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * 위치 정보 관련 WebSocket 컨트롤러
 * 주기적으로 전송되는 위치 데이터는 WebSocket으로 처리
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class GamePlayerSocketController {

    private final GamePlayerFacade gamePlayerFacade;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 플레이어 위치 이동
     * Client sends to: /app/game/player/{gamePlayerId}/move
     * Broadcasts to: /send/game/{gameId}/move
     */
    @MessageMapping("/game/player/{gamePlayerId}/move")
    public void updateLocation(@DestinationVariable String gamePlayerId, @Payload GamePlayerPosUpdateCommand command) {
        GamePlayerPosRedis pos = gamePlayerFacade.updatePlayerLocation(gamePlayerId, command);

        // gameId를 얻기 위해 플레이어 정보 조회
        GamePlayerRedis player = gamePlayerFacade.findPlayerById(gamePlayerId);

        messagingTemplate.convertAndSend("/send/game/" + player.getGameId() + "/move", pos);
    }

    /**
     * 전체 플레이어 위치 조회
     * Client sends to: /app/game/{gameId}/positions
     * Broadcasts to: /send/game/{gameId}/positions
     */
    @MessageMapping("/game/{gameId}/positions")
    public void getAllPlayerPositions(@DestinationVariable String gameId) {
        log.info("전체 플레이어 위치 조회 요청 - 게임ID: {}", gameId);
        List<PlayerPositionResponse> positions = gamePlayerFacade.getAllPlayerPositions(gameId);
        messagingTemplate.convertAndSend("/send/game/" + gameId + "/positions", positions);
    }
}
