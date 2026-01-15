package ctrlg.gyeongdodat.domain.game.service.command;

import ctrlg.gyeongdodat.domain.game.entity.GameRedis;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameCreateCommand {

	private Integer gameTimeSec;

	private Integer hideTimeSec;

	private BigDecimal jailLat;

	private BigDecimal jailLng;

	private String jailImage;

	private String rulesJson;

	public GameRedis toEntity(String id) {
		return GameRedis.builder()
			.id(id)
			.gameTimeSec(gameTimeSec)
			.hideTimeSec(hideTimeSec)
			.jailLat(jailLat)
			.jailLng(jailLng)
			.jailImage(jailImage)
			.rulesJson(rulesJson)
			.build();
	}
}
