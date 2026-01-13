package ctrlg.gyeongdodat.domain.game.entity;

import ctrlg.gyeongdodat.domain.game.enums.WinTeam;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GameRedis Entity 단위 테스트")
class GameRedisTest {

    @Test
    @DisplayName("Builder로 GameRedis를 생성할 수 있어야 한다")
    void shouldCreateGameRedisWithBuilder() {
        // given
        String id = "game-123";
        Integer gameTimeSec = 3600;
        Integer hideTimeSec = 300;
        BigDecimal jailLat = new BigDecimal("37.5665000");
        BigDecimal jailLng = new BigDecimal("126.9780000");
        String jailImage = "http://example.com/jail.png";
        WinTeam winTeam = WinTeam.POLICE;
        String rulesJson = "{\"maxPlayers\": 10}";
        LocalDateTime startedAt = LocalDateTime.now();
        LocalDateTime endedAt = LocalDateTime.now().plusHours(1);
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // when
        GameRedis gameRedis = GameRedis.builder()
                .id(id)
                .gameTimeSec(gameTimeSec)
                .hideTimeSec(hideTimeSec)
                .jailLat(jailLat)
                .jailLng(jailLng)
                .jailImage(jailImage)
                .winTeam(winTeam)
                .rulesJson(rulesJson)
                .startedAt(startedAt)
                .endedAt(endedAt)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        // then
        assertThat(gameRedis.getId()).isEqualTo(id);
        assertThat(gameRedis.getGameTimeSec()).isEqualTo(gameTimeSec);
        assertThat(gameRedis.getHideTimeSec()).isEqualTo(hideTimeSec);
        assertThat(gameRedis.getJailLat()).isEqualTo(jailLat);
        assertThat(gameRedis.getJailLng()).isEqualTo(jailLng);
        assertThat(gameRedis.getJailImage()).isEqualTo(jailImage);
        assertThat(gameRedis.getWinTeam()).isEqualTo(winTeam);
        assertThat(gameRedis.getRulesJson()).isEqualTo(rulesJson);
        assertThat(gameRedis.getStartedAt()).isEqualTo(startedAt);
        assertThat(gameRedis.getEndedAt()).isEqualTo(endedAt);
        assertThat(gameRedis.getCreatedAt()).isEqualTo(createdAt);
        assertThat(gameRedis.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("winTeam을 지정하지 않으면 UNKNOWN으로 기본값이 설정되어야 한다")
    void shouldSetDefaultWinTeamWhenNotProvided() {
        // when
        GameRedis gameRedis = GameRedis.builder()
                .id("game-123")
                .build();

        // then
        assertThat(gameRedis.getWinTeam()).isEqualTo(WinTeam.UNKNOWN);
    }

    @Test
    @DisplayName("createdAt을 지정하지 않으면 현재 시간으로 기본값이 설정되어야 한다")
    void shouldSetDefaultCreatedAtWhenNotProvided() {
        // given
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        // when
        GameRedis gameRedis = GameRedis.builder()
                .id("game-123")
                .build();

        // then
        assertThat(gameRedis.getCreatedAt()).isAfter(before);
    }

    @Test
    @DisplayName("updatedAt을 지정하지 않으면 현재 시간으로 기본값이 설정되어야 한다")
    void shouldSetDefaultUpdatedAtWhenNotProvided() {
        // given
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        // when
        GameRedis gameRedis = GameRedis.builder()
                .id("game-123")
                .build();

        // then
        assertThat(gameRedis.getUpdatedAt()).isAfter(before);
    }
}
