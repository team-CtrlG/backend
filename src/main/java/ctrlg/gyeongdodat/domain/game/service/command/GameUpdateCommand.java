package ctrlg.gyeongdodat.domain.game.service.command;

import ctrlg.gyeongdodat.domain.game.dto.Location;
import ctrlg.gyeongdodat.domain.game.enums.WinTeam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameUpdateCommand {

	private WinTeam winTeam;

	private LocalDateTime startedAt;

  private LocalDateTime endedAt;

  private List<Location> gameArea;

  private String attendanceCode;
}
