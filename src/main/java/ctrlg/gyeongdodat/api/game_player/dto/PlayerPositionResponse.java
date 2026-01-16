package ctrlg.gyeongdodat.api.game_player.dto;

import ctrlg.gyeongdodat.domain.game_player.enums.PlayerStatus;
import ctrlg.gyeongdodat.domain.game_player.enums.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerPositionResponse {

    private String playerId;
    private Team team;
    private PlayerStatus status;
    private BigDecimal lat;
    private BigDecimal lng;
    private Integer thiefNumber;
}
