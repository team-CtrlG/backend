package ctrlg.gyeongdodat.api.game_player.controller;

import ctrlg.gyeongdodat.api.game_player.facade.GamePlayerFacade;
import ctrlg.gyeongdodat.domain.game_player.entity.GamePlayerPosRedis;
import ctrlg.gyeongdodat.domain.game_player.entity.GamePlayerRedis;
import ctrlg.gyeongdodat.domain.game_player.service.command.GamePlayerCreateCommand;
import ctrlg.gyeongdodat.domain.game_player.service.command.GamePlayerPosUpdateCommand;
import ctrlg.gyeongdodat.domain.game_player.service.command.GamePlayerUpdateCommand;
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
public class GamePlayerSocketController {

    private final GamePlayerFacade gamePlayerFacade;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 게임 참여
     * Client sends to: /app/game/join
     * Broadcasts to: /send/game/{gameId}/join
     */
    @MessageMapping("/game/join")
    public void joinGame(@Payload GamePlayerCreateCommand command) {
        log.info("Player {} joining game {}", command.getUserId(), command.getGameId());

        // Facade를 통해 플레이어 생성 및 위치 초기화
        GamePlayerRedis player = gamePlayerFacade.joinGame(command);

        messagingTemplate.convertAndSend("/send/game/" + command.getGameId() + "/join", player);
    }

    /**
     * 게임 내 플레이어 목록 조회
     * Client sends to: /app/game/{gameId}/players
     * Broadcasts to: /send/game/{gameId}/players
     */
    @MessageMapping("/game/{gameId}/players")
    public void findPlayersByGame(@DestinationVariable String gameId) {
        List<GamePlayerRedis> players = gamePlayerFacade.findPlayersByGame(gameId);
        messagingTemplate.convertAndSend("/send/game/" + gameId + "/players", players);
    }

    /**
     * 플레이어 상태 업데이트
     * Client sends to: /app/player/{gamePlayerId}/update
     * Broadcasts to: /send/game/{gameId}/player/updated
     */
    @MessageMapping("/player/{gamePlayerId}/update")
    public void updatePlayer(@DestinationVariable String gamePlayerId, @Payload GamePlayerUpdateCommand command) {
        GamePlayerRedis player = gamePlayerFacade.updatePlayerStatus(gamePlayerId, command);
        messagingTemplate.convertAndSend("/send/game/" + player.getGameId() + "/player/updated", player);
    }

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
     * 플레이어 나가기
     * Client sends to: /app/player/{gamePlayerId}/leave
     * Broadcasts to: /send/game/{gameId}/leave
     */
    @MessageMapping("/player/{gamePlayerId}/leave")
    public void leaveGame(@DestinationVariable String gamePlayerId) {
        String gameId = gamePlayerFacade.leaveGame(gamePlayerId);

        if (gameId != null) {
            messagingTemplate.convertAndSend("/send/game/" + gameId + "/leave", gamePlayerId);
        }
    }
}