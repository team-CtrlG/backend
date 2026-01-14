package ctrlg.gyeongdodat.domain.game_log.service.command;

import ctrlg.gyeongdodat.domain.game_log.entity.GameLogRedis;
import ctrlg.gyeongdodat.domain.game_log.enums.GameAction;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GameLogCreateCommand {

    private String gameId;

    private String actorPlayerId;

    private String targetPlayerId;

    private GameAction action;

    public GameLogRedis toEntity(Long id) {
        return GameLogRedis.builder()
                .id(id)
                .gameId(gameId)
                .actorPlayerId(actorPlayerId)
                .targetPlayerId(targetPlayerId)
                .action(action)
                .build();
    }
}
