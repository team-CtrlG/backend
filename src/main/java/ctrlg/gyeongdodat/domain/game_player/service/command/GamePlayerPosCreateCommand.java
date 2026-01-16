package ctrlg.gyeongdodat.domain.game_player.service.command;

import ctrlg.gyeongdodat.domain.game_player.entity.GamePlayerPosRedis;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GamePlayerPosCreateCommand {

	private String gamePlayerId;

	private BigDecimal lat;

	private BigDecimal lng;

	public GamePlayerPosRedis toEntity() {
		return GamePlayerPosRedis.builder()
			.gamePlayerId(gamePlayerId)
			.lat(lat)
			.lng(lng)
			.build();
	}
}
