package ctrlg.gyeongdodat.domain.game_player.service.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GamePlayerPosUpdateCommand {

	private BigDecimal lat;

	private BigDecimal lng;
}
