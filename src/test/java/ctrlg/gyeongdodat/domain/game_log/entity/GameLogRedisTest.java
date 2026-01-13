package ctrlg.gyeongdodat.domain.game_log.entity;

import ctrlg.gyeongdodat.domain.game_log.enums.GameAction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("GameLogRedis Entity 단위 테스트")
class GameLogRedisTest {

    @Test
    @DisplayName("Builder로 GameLogRedis를 생성할 수 있어야 한다")
    void shouldCreateGameLogRedisWithBuilder() {
        // given
        Long id = 1L;
        String gameId = "game-123";
        String actorPlayerId = "player-1";
        String targetPlayerId = "player-2";
        GameAction action = GameAction.ARREST;
        LocalDateTime createdAt = LocalDateTime.now();

        // when
        GameLogRedis log = GameLogRedis.builder()
                .id(id)
                .gameId(gameId)
                .actorPlayerId(actorPlayerId)
                .targetPlayerId(targetPlayerId)
                .action(action)
                .createdAt(createdAt)
                .build();

        // then
        assertThat(log.getId()).isEqualTo(id);
        assertThat(log.getGameId()).isEqualTo(gameId);
        assertThat(log.getActorPlayerId()).isEqualTo(actorPlayerId);
        assertThat(log.getTargetPlayerId()).isEqualTo(targetPlayerId);
        assertThat(log.getAction()).isEqualTo(action);
        assertThat(log.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("createdAt을 지정하지 않으면 현재 시간으로 기본값이 설정되어야 한다")
    void shouldSetDefaultCreatedAtWhenNotProvided() {
        // given
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        // when
        GameLogRedis log = GameLogRedis.builder()
                .id(1L)
                .gameId("game-123")
                .actorPlayerId("player-1")
                .targetPlayerId("player-2")
                .action(GameAction.ARREST)
                .build();

        // then
        assertThat(log.getCreatedAt()).isAfter(before);
    }

    @Test
    @DisplayName("모든 GameAction 값으로 생성할 수 있어야 한다")
    void shouldCreateWithAllGameActions() {
        for (GameAction action : GameAction.values()) {
            // when
            GameLogRedis log = GameLogRedis.builder()
                    .id(1L)
                    .gameId("game-123")
                    .actorPlayerId("player-1")
                    .targetPlayerId("player-2")
                    .action(action)
                    .build();

            // then
            assertThat(log.getAction()).isEqualTo(action);
        }
    }
}
