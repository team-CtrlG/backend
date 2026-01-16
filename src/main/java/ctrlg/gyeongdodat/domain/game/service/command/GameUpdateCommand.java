package ctrlg.gyeongdodat.domain.game.service.command;

import ctrlg.gyeongdodat.domain.game.dto.Location;
import ctrlg.gyeongdodat.domain.game.enums.WinTeam;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class GameUpdateCommand {

    private WinTeam winTeam;

    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    private List<Location> gameArea;
}
