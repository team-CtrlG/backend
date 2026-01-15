package ctrlg.gyeongdodat.domain.game.service.command;

import ctrlg.gyeongdodat.domain.game.enums.WinTeam;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GameUpdateCommand {

    private WinTeam winTeam;

    private LocalDateTime startedAt;

    private LocalDateTime endedAt;
}
