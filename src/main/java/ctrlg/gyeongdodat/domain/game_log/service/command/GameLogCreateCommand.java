package ctrlg.gyeongdodat.domain.game_log.service.command;

import ctrlg.gyeongdodat.domain.game_log.entity.GameLogRedis;
import ctrlg.gyeongdodat.domain.game_log.enums.GameAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameLogCreateCommand {

	private String gameId;

	private String actorPlayerId;

	private String targetPlayerId;

	private GameAction action;

	public GameLogRedis toEntity(String id) {
		return GameLogRedis.builder()
			.id(id)
			.gameId(gameId)
			.actorPlayerId(actorPlayerId)
			.targetPlayerId(targetPlayerId)
			.action(action)
			.build();
	}
}
