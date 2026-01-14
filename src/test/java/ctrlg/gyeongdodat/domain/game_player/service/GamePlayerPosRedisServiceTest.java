package ctrlg.gyeongdodat.domain.game_player.service;

import ctrlg.gyeongdodat.domain.game_player.entity.GamePlayerPosRedis;
import ctrlg.gyeongdodat.domain.game_player.repository.GamePlayerPosRedisRepository;
import ctrlg.gyeongdodat.domain.game_player.service.command.GamePlayerPosCreateCommand;
import ctrlg.gyeongdodat.domain.game_player.service.command.GamePlayerPosUpdateCommand;
import ctrlg.gyeongdodat.global.exception.ErrorCode;
import ctrlg.gyeongdodat.global.exception.GlobalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GamePlayerPosRedisServiceTest {

    @InjectMocks
    private GamePlayerPosRedisService gamePlayerPosRedisService;

    @Mock
    private GamePlayerPosRedisRepository gamePlayerPosRedisRepository;

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("정상적으로 게임 플레이어 위치를 생성한다")
        void createGamePlayerPos() {
            // given
            GamePlayerPosCreateCommand command = GamePlayerPosCreateCommand.builder()
                    .gamePlayerId("player-123")
                    .lat(new BigDecimal("37.5665"))
                    .lng(new BigDecimal("126.9780"))
                    .build();

            GamePlayerPosRedis savedPos = GamePlayerPosRedis.builder()
                    .gamePlayerId("player-123")
                    .lat(new BigDecimal("37.5665"))
                    .lng(new BigDecimal("126.9780"))
                    .build();

            given(gamePlayerPosRedisRepository.save(any(GamePlayerPosRedis.class))).willReturn(savedPos);

            // when
            GamePlayerPosRedis result = gamePlayerPosRedisService.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getGamePlayerId()).isEqualTo("player-123");
            assertThat(result.getLat()).isEqualByComparingTo(new BigDecimal("37.5665"));
            verify(gamePlayerPosRedisRepository).save(any(GamePlayerPosRedis.class));
        }
    }

    @Nested
    @DisplayName("findByGamePlayerId 메서드")
    class FindByGamePlayerId {

        @Test
        @DisplayName("gamePlayerId로 위치를 정상 조회한다")
        void findByGamePlayerId() {
            // given
            String gamePlayerId = "player-123";
            GamePlayerPosRedis pos = GamePlayerPosRedis.builder()
                    .gamePlayerId(gamePlayerId)
                    .lat(new BigDecimal("37.5665"))
                    .lng(new BigDecimal("126.9780"))
                    .build();

            given(gamePlayerPosRedisRepository.findById(gamePlayerId)).willReturn(Optional.of(pos));

            // when
            GamePlayerPosRedis result = gamePlayerPosRedisService.findByGamePlayerId(gamePlayerId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getGamePlayerId()).isEqualTo(gamePlayerId);
        }

        @Test
        @DisplayName("존재하지 않는 gamePlayerId로 조회하면 예외가 발생한다")
        void findByGamePlayerIdNotFound() {
            // given
            String gamePlayerId = "non-existent-id";
            given(gamePlayerPosRedisRepository.findById(gamePlayerId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> gamePlayerPosRedisService.findByGamePlayerId(gamePlayerId))
                    .isInstanceOf(GlobalException.class)
                    .extracting(e -> ((GlobalException) e).getErrorCode())
                    .isEqualTo(ErrorCode.GAME_PLAYER_POS_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("findByGamePlayerIdOrNull 메서드")
    class FindByGamePlayerIdOrNull {

        @Test
        @DisplayName("gamePlayerId로 위치를 정상 조회한다")
        void findByGamePlayerIdOrNull() {
            // given
            String gamePlayerId = "player-123";
            GamePlayerPosRedis pos = GamePlayerPosRedis.builder()
                    .gamePlayerId(gamePlayerId)
                    .build();

            given(gamePlayerPosRedisRepository.findById(gamePlayerId)).willReturn(Optional.of(pos));

            // when
            GamePlayerPosRedis result = gamePlayerPosRedisService.findByGamePlayerIdOrNull(gamePlayerId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getGamePlayerId()).isEqualTo(gamePlayerId);
        }

        @Test
        @DisplayName("존재하지 않는 gamePlayerId로 조회하면 null을 반환한다")
        void findByGamePlayerIdOrNullReturnsNull() {
            // given
            String gamePlayerId = "non-existent-id";
            given(gamePlayerPosRedisRepository.findById(gamePlayerId)).willReturn(Optional.empty());

            // when
            GamePlayerPosRedis result = gamePlayerPosRedisService.findByGamePlayerIdOrNull(gamePlayerId);

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("findAll 메서드")
    class FindAll {

        @Test
        @DisplayName("전체 위치 목록을 조회한다")
        void findAll() {
            // given
            GamePlayerPosRedis pos1 = GamePlayerPosRedis.builder().gamePlayerId("player-1").build();
            GamePlayerPosRedis pos2 = GamePlayerPosRedis.builder().gamePlayerId("player-2").build();

            given(gamePlayerPosRedisRepository.findAll()).willReturn(List.of(pos1, pos2));

            // when
            List<GamePlayerPosRedis> result = gamePlayerPosRedisService.findAll();

            // then
            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("update 메서드")
    class Update {

        @Test
        @DisplayName("정상적으로 위치를 업데이트한다")
        void updateGamePlayerPos() {
            // given
            String gamePlayerId = "player-123";
            GamePlayerPosRedis pos = GamePlayerPosRedis.builder()
                    .gamePlayerId(gamePlayerId)
                    .lat(new BigDecimal("37.5665"))
                    .lng(new BigDecimal("126.9780"))
                    .build();

            GamePlayerPosUpdateCommand command = GamePlayerPosUpdateCommand.builder()
                    .lat(new BigDecimal("37.5700"))
                    .lng(new BigDecimal("126.9800"))
                    .build();

            given(gamePlayerPosRedisRepository.findById(gamePlayerId)).willReturn(Optional.of(pos));
            given(gamePlayerPosRedisRepository.save(any(GamePlayerPosRedis.class))).willReturn(pos);

            // when
            GamePlayerPosRedis result = gamePlayerPosRedisService.update(gamePlayerId, command);

            // then
            assertThat(result.getLat()).isEqualByComparingTo(new BigDecimal("37.5700"));
            assertThat(result.getLng()).isEqualByComparingTo(new BigDecimal("126.9800"));
            verify(gamePlayerPosRedisRepository).save(pos);
        }

        @Test
        @DisplayName("존재하지 않는 gamePlayerId로 업데이트하면 예외가 발생한다")
        void updateGamePlayerPosNotFound() {
            // given
            String gamePlayerId = "non-existent-id";
            GamePlayerPosUpdateCommand command = GamePlayerPosUpdateCommand.builder()
                    .lat(new BigDecimal("37.5700"))
                    .build();

            given(gamePlayerPosRedisRepository.findById(gamePlayerId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> gamePlayerPosRedisService.update(gamePlayerId, command))
                    .isInstanceOf(GlobalException.class)
                    .extracting(e -> ((GlobalException) e).getErrorCode())
                    .isEqualTo(ErrorCode.GAME_PLAYER_POS_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("delete 메서드")
    class Delete {

        @Test
        @DisplayName("정상적으로 위치를 삭제한다")
        void deleteGamePlayerPos() {
            // given
            String gamePlayerId = "player-123";
            given(gamePlayerPosRedisRepository.existsById(gamePlayerId)).willReturn(true);

            // when
            gamePlayerPosRedisService.delete(gamePlayerId);

            // then
            verify(gamePlayerPosRedisRepository).deleteById(gamePlayerId);
        }

        @Test
        @DisplayName("존재하지 않는 gamePlayerId로 삭제하면 예외가 발생한다")
        void deleteGamePlayerPosNotFound() {
            // given
            String gamePlayerId = "non-existent-id";
            given(gamePlayerPosRedisRepository.existsById(gamePlayerId)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> gamePlayerPosRedisService.delete(gamePlayerId))
                    .isInstanceOf(GlobalException.class)
                    .extracting(e -> ((GlobalException) e).getErrorCode())
                    .isEqualTo(ErrorCode.GAME_PLAYER_POS_NOT_FOUND);
        }
    }
}
