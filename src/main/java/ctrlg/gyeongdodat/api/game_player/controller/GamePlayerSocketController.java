package ctrlg.gyeongdodat.api.game_player.controller;

import ctrlg.gyeongdodat.api.game_player.facade.GamePlayerFacade;
import ctrlg.gyeongdodat.domain.game_player.service.command.GamePlayerPosUpdateCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

/**
 * 위치 정보 관련 WebSocket 컨트롤러
 * 클라이언트로부터 위치 데이터를 수신하여 저장
 * 위치 브로드캐스트는 GameSchedulerService에서 5초 주기로 처리
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class GamePlayerSocketController {

    private final GamePlayerFacade gamePlayerFacade;

    /**
     * 플레이어 위치 이동
     * Client sends to: /app/game/player/{gamePlayerId}/move
     * 위치 정보를 저장하고 반환값 없음 (주기적 브로드캐스트로 전달)
     */
    @MessageMapping("/game/player/{gamePlayerId}/move")
    public void updateLocation(@DestinationVariable String gamePlayerId, @Payload GamePlayerPosUpdateCommand command) {
        gamePlayerFacade.updatePlayerLocation(gamePlayerId, command);
    }
}
