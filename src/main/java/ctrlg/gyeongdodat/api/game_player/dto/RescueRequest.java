package ctrlg.gyeongdodat.api.game_player.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RescueRequest {

    private String rescuerPlayerId;
    private Integer targetThiefNumber;
}
