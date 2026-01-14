package ctrlg.gyeongdodat.domain.game_player.service.command;

import ctrlg.gyeongdodat.domain.game_player.enums.ConnectionState;
import ctrlg.gyeongdodat.domain.game_player.enums.PlayerStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GamePlayerUpdateCommand {

    private Integer caughtCount;

    private Integer escapeCount;

    private Integer stepCount;

    private Integer distanceM;

    private Integer jailedJailNo;

    private Boolean attendanceYn;

    private PlayerStatus status;

    private ConnectionState connectionState;

    private Integer heartbeatRetryCount;

    private LocalDateTime heartbeatLastAt;
}
