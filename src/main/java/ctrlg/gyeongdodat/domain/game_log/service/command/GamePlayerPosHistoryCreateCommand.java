package ctrlg.gyeongdodat.domain.game_log.service.command;

import ctrlg.gyeongdodat.domain.game_log.entity.GamePlayerPosHistoryRedis;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GamePlayerPosHistoryCreateCommand {

	private String gamePlayerId;

	private BigDecimal lat;

	private BigDecimal lng;

	public GamePlayerPosHistoryRedis toEntity(Long id) {
		return GamePlayerPosHistoryRedis.builder()
			.id(id)
			.gamePlayerId(gamePlayerId)
			.lat(lat)
			.lng(lng)
			.build();
	}
}
