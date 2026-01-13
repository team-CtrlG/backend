package ctrlg.gyeongdodat.domain.game_player.entity;

import ctrlg.gyeongdodat.domain.game_player.enums.ConnectionState;
import ctrlg.gyeongdodat.domain.game_player.enums.PlayerRole;
import ctrlg.gyeongdodat.domain.game_player.enums.PlayerStatus;
import ctrlg.gyeongdodat.domain.game_player.enums.Team;
import ctrlg.gyeongdodat.global.entity.BaseRedisTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "game_player")
public class GamePlayerRedis extends BaseRedisTimeEntity {

    @Id
    private String id;

    @Indexed
    private String gameId;

    private String userId;

    private PlayerRole role;

    private Team team;

    private int caughtCount;

    private int escapeCount;

    private int stepCount;

    private int distanceM;

    private Integer jailedJailNo;

    private boolean attendanceYn;

    private PlayerStatus status;

    @Builder.Default
    private ConnectionState connectionState = ConnectionState.UNKNOWN;

    private int heartbeatRetryCount;

    private LocalDateTime heartbeatLastAt;
}
