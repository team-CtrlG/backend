package ctrlg.gyeongdodat.domain.game.service.command;

import ctrlg.gyeongdodat.domain.game.enums.WinTeam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameUpdateCommand {

	private WinTeam winTeam;

	private LocalDateTime startedAt;

	private LocalDateTime endedAt;
}
