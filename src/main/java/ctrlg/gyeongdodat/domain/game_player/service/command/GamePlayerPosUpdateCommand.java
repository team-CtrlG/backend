package ctrlg.gyeongdodat.domain.game_player.service.command;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class GamePlayerPosUpdateCommand {

    private BigDecimal lat;

    private BigDecimal lng;
}
