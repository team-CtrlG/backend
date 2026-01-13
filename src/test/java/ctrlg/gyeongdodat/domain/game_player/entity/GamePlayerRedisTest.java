package ctrlg.gyeongdodat.domain.game_player.entity;

import ctrlg.gyeongdodat.domain.game_player.enums.ConnectionState;
import ctrlg.gyeongdodat.domain.game_player.enums.PlayerRole;
import ctrlg.gyeongdodat.domain.game_player.enums.PlayerStatus;
import ctrlg.gyeongdodat.domain.game_player.enums.Team;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GamePlayerRedis Entity 단위 테스트")
class GamePlayerRedisTest {

    @Test
    @DisplayName("Builder로 GamePlayerRedis를 생성할 수 있어야 한다")
    void shouldCreateGamePlayerRedisWithBuilder() {
        // given
        String id = "player-123";
        String gameId = "game-123";
        String userId = "user-123";
        PlayerRole role = PlayerRole.PLAYER;
        Team team = Team.POLICE;
        int caughtCount = 5;
        int escapeCount = 3;
        int stepCount = 1000;
        int distanceM = 5000;
        Integer jailedJailNo = 1;
        boolean attendanceYn = true;
        PlayerStatus status = PlayerStatus.JAILED;
        ConnectionState connectionState = ConnectionState.CONNECTED;
        int heartbeatRetryCount = 0;
        LocalDateTime heartbeatLastAt = LocalDateTime.now();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // when
        GamePlayerRedis player = GamePlayerRedis.builder()
                .id(id)
                .gameId(gameId)
                .userId(userId)
                .role(role)
                .team(team)
                .caughtCount(caughtCount)
                .escapeCount(escapeCount)
                .stepCount(stepCount)
                .distanceM(distanceM)
                .jailedJailNo(jailedJailNo)
                .attendanceYn(attendanceYn)
                .status(status)
                .connectionState(connectionState)
                .heartbeatRetryCount(heartbeatRetryCount)
                .heartbeatLastAt(heartbeatLastAt)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        // then
        assertThat(player.getId()).isEqualTo(id);
        assertThat(player.getGameId()).isEqualTo(gameId);
        assertThat(player.getUserId()).isEqualTo(userId);
        assertThat(player.getRole()).isEqualTo(role);
        assertThat(player.getTeam()).isEqualTo(team);
        assertThat(player.getCaughtCount()).isEqualTo(caughtCount);
        assertThat(player.getEscapeCount()).isEqualTo(escapeCount);
        assertThat(player.getStepCount()).isEqualTo(stepCount);
        assertThat(player.getDistanceM()).isEqualTo(distanceM);
        assertThat(player.getJailedJailNo()).isEqualTo(jailedJailNo);
        assertThat(player.isAttendanceYn()).isEqualTo(attendanceYn);
        assertThat(player.getStatus()).isEqualTo(status);
        assertThat(player.getConnectionState()).isEqualTo(connectionState);
        assertThat(player.getHeartbeatRetryCount()).isEqualTo(heartbeatRetryCount);
        assertThat(player.getHeartbeatLastAt()).isEqualTo(heartbeatLastAt);
        assertThat(player.getCreatedAt()).isEqualTo(createdAt);
        assertThat(player.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("connectionState를 지정하지 않으면 UNKNOWN으로 기본값이 설정되어야 한다")
    void shouldSetDefaultConnectionStateWhenNotProvided() {
        // when
        GamePlayerRedis player = GamePlayerRedis.builder()
                .id("player-123")
                .build();

        // then
        assertThat(player.getConnectionState()).isEqualTo(ConnectionState.UNKNOWN);
    }

    @Test
    @DisplayName("createdAt을 지정하지 않으면 현재 시간으로 기본값이 설정되어야 한다")
    void shouldSetDefaultCreatedAtWhenNotProvided() {
        // given
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        // when
        GamePlayerRedis player = GamePlayerRedis.builder()
                .id("player-123")
                .build();

        // then
        assertThat(player.getCreatedAt()).isAfter(before);
    }

    @Test
    @DisplayName("updatedAt을 지정하지 않으면 현재 시간으로 기본값이 설정되어야 한다")
    void shouldSetDefaultUpdatedAtWhenNotProvided() {
        // given
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        // when
        GamePlayerRedis player = GamePlayerRedis.builder()
                .id("player-123")
                .build();

        // then
        assertThat(player.getUpdatedAt()).isAfter(before);
    }

    @Test
    @DisplayName("userId가 null이어도 생성할 수 있어야 한다")
    void shouldAllowNullUserId() {
        // when
        GamePlayerRedis player = GamePlayerRedis.builder()
                .id("player-123")
                .userId(null)
                .build();

        // then
        assertThat(player.getUserId()).isNull();
    }

    @Test
    @DisplayName("jailedJailNo가 null이어도 생성할 수 있어야 한다")
    void shouldAllowNullJailedJailNo() {
        // when
        GamePlayerRedis player = GamePlayerRedis.builder()
                .id("player-123")
                .jailedJailNo(null)
                .build();

        // then
        assertThat(player.getJailedJailNo()).isNull();
    }
}
