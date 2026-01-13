package ctrlg.gyeongdodat.domain.game_log.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GamePlayerPosHistoryRedis Entity 단위 테스트")
class GamePlayerPosHistoryRedisTest {

    @Test
    @DisplayName("Builder로 GamePlayerPosHistoryRedis를 생성할 수 있어야 한다")
    void shouldCreateGamePlayerPosHistoryRedisWithBuilder() {
        // given
        Long id = 1L;
        String gamePlayerId = "player-123";
        BigDecimal lat = new BigDecimal("37.5665000");
        BigDecimal lng = new BigDecimal("126.9780000");
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // when
        GamePlayerPosHistoryRedis history = GamePlayerPosHistoryRedis.builder()
                .id(id)
                .gamePlayerId(gamePlayerId)
                .lat(lat)
                .lng(lng)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        // then
        assertThat(history.getId()).isEqualTo(id);
        assertThat(history.getGamePlayerId()).isEqualTo(gamePlayerId);
        assertThat(history.getLat()).isEqualTo(lat);
        assertThat(history.getLng()).isEqualTo(lng);
        assertThat(history.getCreatedAt()).isEqualTo(createdAt);
        assertThat(history.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("createdAt이 기본값으로 현재 시간이 설정되어야 한다")
    void shouldSetDefaultCreatedAt() {
        // given
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        // when
        GamePlayerPosHistoryRedis history = GamePlayerPosHistoryRedis.builder()
                .id(1L)
                .gamePlayerId("player-123")
                .lat(new BigDecimal("37.5665000"))
                .lng(new BigDecimal("126.9780000"))
                .build();

        // then
        assertThat(history.getCreatedAt()).isAfter(before);
    }

    @Test
    @DisplayName("updatedAt이 기본값으로 현재 시간이 설정되어야 한다")
    void shouldSetDefaultUpdatedAt() {
        // given
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        // when
        GamePlayerPosHistoryRedis history = GamePlayerPosHistoryRedis.builder()
                .id(1L)
                .gamePlayerId("player-123")
                .lat(new BigDecimal("37.5665000"))
                .lng(new BigDecimal("126.9780000"))
                .build();

        // then
        assertThat(history.getUpdatedAt()).isAfter(before);
    }

    @Test
    @DisplayName("좌표 정밀도가 유지되어야 한다")
    void shouldMaintainCoordinatePrecision() {
        // given
        BigDecimal lat = new BigDecimal("37.5665123");
        BigDecimal lng = new BigDecimal("126.9780456");

        // when
        GamePlayerPosHistoryRedis history = GamePlayerPosHistoryRedis.builder()
                .id(1L)
                .gamePlayerId("player-123")
                .lat(lat)
                .lng(lng)
                .build();

        // then
        assertThat(history.getLat()).isEqualByComparingTo(lat);
        assertThat(history.getLng()).isEqualByComparingTo(lng);
    }
}
