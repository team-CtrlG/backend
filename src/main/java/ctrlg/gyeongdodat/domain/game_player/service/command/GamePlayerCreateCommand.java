package ctrlg.gyeongdodat.domain.game_player.service.command;

import ctrlg.gyeongdodat.domain.game_player.entity.GamePlayerRedis;
import ctrlg.gyeongdodat.domain.game_player.enums.PlayerRole;
import ctrlg.gyeongdodat.domain.game_player.enums.Team;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GamePlayerCreateCommand {

    private String gameId;

    private String userId;

    private PlayerRole role;

    private Team team;

    public GamePlayerRedis toEntity(String id) {
        return GamePlayerRedis.builder()
                .id(id)
                .gameId(gameId)
                .userId(userId)
                .role(role)
                .team(team)
                .build();
    }
}
