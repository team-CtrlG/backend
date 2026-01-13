package ctrlg.gyeongdodat.domain.game_player.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GamePlayerPosRedis Entity 단위 테스트")
class GamePlayerPosRedisTest {

    @Test
    @DisplayName("Builder로 GamePlayerPosRedis를 생성할 수 있어야 한다")
    void shouldCreateGamePlayerPosRedisWithBuilder() {
        // given
        String gamePlayerId = "player-123";
        BigDecimal lat = new BigDecimal("37.5665000");
        BigDecimal lng = new BigDecimal("126.9780000");
        LocalDateTime updatedAt = LocalDateTime.now();

        // when
        GamePlayerPosRedis pos = GamePlayerPosRedis.builder()
                .gamePlayerId(gamePlayerId)
                .lat(lat)
                .lng(lng)
                .updatedAt(updatedAt)
                .build();

        // then
        assertThat(pos.getGamePlayerId()).isEqualTo(gamePlayerId);
        assertThat(pos.getLat()).isEqualTo(lat);
        assertThat(pos.getLng()).isEqualTo(lng);
        assertThat(pos.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("updatedAt을 지정하지 않으면 현재 시간으로 기본값이 설정되어야 한다")
    void shouldSetDefaultUpdatedAtWhenNotProvided() {
        // given
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        // when
        GamePlayerPosRedis pos = GamePlayerPosRedis.builder()
                .gamePlayerId("player-123")
                .lat(new BigDecimal("37.5665000"))
                .lng(new BigDecimal("126.9780000"))
                .build();

        // then
        assertThat(pos.getUpdatedAt()).isAfter(before);
    }

    @Test
    @DisplayName("좌표 정밀도가 유지되어야 한다")
    void shouldMaintainCoordinatePrecision() {
        // given
        BigDecimal lat = new BigDecimal("37.5665123");
        BigDecimal lng = new BigDecimal("126.9780456");

        // when
        GamePlayerPosRedis pos = GamePlayerPosRedis.builder()
                .gamePlayerId("player-123")
                .lat(lat)
                .lng(lng)
                .build();

        // then
        assertThat(pos.getLat()).isEqualByComparingTo(lat);
        assertThat(pos.getLng()).isEqualByComparingTo(lng);
    }
}
