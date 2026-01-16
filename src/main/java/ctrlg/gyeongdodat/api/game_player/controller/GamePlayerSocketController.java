package ctrlg.gyeongdodat.api.game_player.controller;

import ctrlg.gyeongdodat.api.game_player.dto.ArrestRequest;
import ctrlg.gyeongdodat.api.game_player.dto.PlayerPositionResponse;
import ctrlg.gyeongdodat.api.game_player.dto.RescueRequest;
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
        log.info("게임 참가 요청 - 유저ID: {}, 게임ID: {}", command.getUserId(), command.getGameId());

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

    /**
     * 체포: 경찰이 도둑을 체포
     * Client sends to: /app/game/{gameId}/arrest
     * Broadcasts to: /send/game/{gameId}/arrest
     */
    @MessageMapping("/game/{gameId}/arrest")
    public void arrestThief(@DestinationVariable String gameId, @Payload ArrestRequest request) {
        log.info("체포 요청 - 경찰ID: {}, 도둑번호: {}, 게임ID: {}", request.getPolicePlayerId(), request.getThiefNumber(), gameId);
        GamePlayerRedis arrestedThief = gamePlayerFacade.arrestThief(gameId, request.getPolicePlayerId(), request.getThiefNumber());
        messagingTemplate.convertAndSend("/send/game/" + gameId + "/arrest", arrestedThief);
    }

    /**
     * 구출: 도둑이 수감된 동료를 구출
     * Client sends to: /app/game/{gameId}/rescue
     * Broadcasts to: /send/game/{gameId}/rescue
     */
    @MessageMapping("/game/{gameId}/rescue")
    public void rescueThief(@DestinationVariable String gameId, @Payload RescueRequest request) {
        log.info("구출 요청 - 구출자ID: {}, 대상도둑번호: {}, 게임ID: {}", request.getRescuerPlayerId(), request.getTargetThiefNumber(), gameId);
        GamePlayerRedis rescuedThief = gamePlayerFacade.rescueThief(gameId, request.getRescuerPlayerId(), request.getTargetThiefNumber());
        messagingTemplate.convertAndSend("/send/game/" + gameId + "/rescue", rescuedThief);
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