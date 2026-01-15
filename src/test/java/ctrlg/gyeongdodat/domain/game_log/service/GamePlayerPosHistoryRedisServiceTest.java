package ctrlg.gyeongdodat.domain.game_log.service;

import ctrlg.gyeongdodat.domain.game_log.entity.GamePlayerPosHistoryRedis;
import ctrlg.gyeongdodat.domain.game_log.repository.GamePlayerPosHistoryRedisRepository;
import ctrlg.gyeongdodat.domain.game_log.service.command.GamePlayerPosHistoryCreateCommand;
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
class GamePlayerPosHistoryRedisServiceTest {

    @InjectMocks
    private GamePlayerPosHistoryRedisService gamePlayerPosHistoryRedisService;

    @Mock
    private GamePlayerPosHistoryRedisRepository gamePlayerPosHistoryRedisRepository;

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("정상적으로 위치 이력을 생성한다")
        void createPosHistory() {
            // given
            GamePlayerPosHistoryCreateCommand command = GamePlayerPosHistoryCreateCommand.builder()
                    .gamePlayerId("player-123")
                    .lat(new BigDecimal("37.5665"))
                    .lng(new BigDecimal("126.9780"))
                    .build();

            GamePlayerPosHistoryRedis savedHistory = GamePlayerPosHistoryRedis.builder()
                    .id(1L)
                    .gamePlayerId("player-123")
                    .lat(new BigDecimal("37.5665"))
                    .lng(new BigDecimal("126.9780"))
                    .build();

            given(gamePlayerPosHistoryRedisRepository.save(any(GamePlayerPosHistoryRedis.class))).willReturn(savedHistory);

            // when
            GamePlayerPosHistoryRedis result = gamePlayerPosHistoryRedisService.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getGamePlayerId()).isEqualTo("player-123");
            assertThat(result.getLat()).isEqualByComparingTo(new BigDecimal("37.5665"));
            verify(gamePlayerPosHistoryRedisRepository).save(any(GamePlayerPosHistoryRedis.class));
        }
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("ID로 위치 이력을 정상 조회한다")
        void findById() {
            // given
            Long historyId = 1L;
            GamePlayerPosHistoryRedis history = GamePlayerPosHistoryRedis.builder()
                    .id(historyId)
                    .gamePlayerId("player-123")
                    .build();

            given(gamePlayerPosHistoryRedisRepository.findById(historyId)).willReturn(Optional.of(history));

            // when
            GamePlayerPosHistoryRedis result = gamePlayerPosHistoryRedisService.findById(historyId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(historyId);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 예외가 발생한다")
        void findByIdNotFound() {
            // given
            Long historyId = 999L;
            given(gamePlayerPosHistoryRedisRepository.findById(historyId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> gamePlayerPosHistoryRedisService.findById(historyId))
                    .isInstanceOf(GlobalException.class)
                    .extracting(e -> ((GlobalException) e).getErrorCode())
                    .isEqualTo(ErrorCode.GAME_PLAYER_POS_HISTORY_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("findByGamePlayerId 메서드")
    class FindByGamePlayerId {

        @Test
        @DisplayName("gamePlayerId로 위치 이력 목록을 조회한다")
        void findByGamePlayerId() {
            // given
            String gamePlayerId = "player-123";
            GamePlayerPosHistoryRedis history1 = GamePlayerPosHistoryRedis.builder().id(1L).gamePlayerId(gamePlayerId).build();
            GamePlayerPosHistoryRedis history2 = GamePlayerPosHistoryRedis.builder().id(2L).gamePlayerId(gamePlayerId).build();

            given(gamePlayerPosHistoryRedisRepository.findByGamePlayerId(gamePlayerId)).willReturn(List.of(history1, history2));

            // when
            List<GamePlayerPosHistoryRedis> result = gamePlayerPosHistoryRedisService.findByGamePlayerId(gamePlayerId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).extracting(GamePlayerPosHistoryRedis::getGamePlayerId)
                    .containsOnly(gamePlayerId);
        }
    }

    @Nested
    @DisplayName("findAll 메서드")
    class FindAll {

        @Test
        @DisplayName("전체 위치 이력 목록을 조회한다")
        void findAll() {
            // given
            GamePlayerPosHistoryRedis history1 = GamePlayerPosHistoryRedis.builder().id(1L).build();
            GamePlayerPosHistoryRedis history2 = GamePlayerPosHistoryRedis.builder().id(2L).build();

            given(gamePlayerPosHistoryRedisRepository.findAll()).willReturn(List.of(history1, history2));

            // when
            List<GamePlayerPosHistoryRedis> result = gamePlayerPosHistoryRedisService.findAll();

            // then
            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("delete 메서드")
    class Delete {

        @Test
        @DisplayName("정상적으로 위치 이력을 삭제한다")
        void deletePosHistory() {
            // given
            Long historyId = 1L;
            given(gamePlayerPosHistoryRedisRepository.existsById(historyId)).willReturn(true);

            // when
            gamePlayerPosHistoryRedisService.delete(historyId);

            // then
            verify(gamePlayerPosHistoryRedisRepository).deleteById(historyId);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 삭제하면 예외가 발생한다")
        void deletePosHistoryNotFound() {
            // given
            Long historyId = 999L;
            given(gamePlayerPosHistoryRedisRepository.existsById(historyId)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> gamePlayerPosHistoryRedisService.delete(historyId))
                    .isInstanceOf(GlobalException.class)
                    .extracting(e -> ((GlobalException) e).getErrorCode())
                    .isEqualTo(ErrorCode.GAME_PLAYER_POS_HISTORY_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("deleteByGamePlayerId 메서드")
    class DeleteByGamePlayerId {

        @Test
        @DisplayName("gamePlayerId로 위치 이력들을 일괄 삭제한다")
        void deleteByGamePlayerId() {
            // given
            String gamePlayerId = "player-123";
            GamePlayerPosHistoryRedis history1 = GamePlayerPosHistoryRedis.builder().id(1L).gamePlayerId(gamePlayerId).build();
            GamePlayerPosHistoryRedis history2 = GamePlayerPosHistoryRedis.builder().id(2L).gamePlayerId(gamePlayerId).build();

            given(gamePlayerPosHistoryRedisRepository.findByGamePlayerId(gamePlayerId)).willReturn(List.of(history1, history2));

            // when
            gamePlayerPosHistoryRedisService.deleteByGamePlayerId(gamePlayerId);

            // then
            verify(gamePlayerPosHistoryRedisRepository).deleteAll(List.of(history1, history2));
        }
    }
}
