package ctrlg.gyeongdodat.domain.game_log.service;

import ctrlg.gyeongdodat.domain.game_log.entity.GameLogRedis;
import ctrlg.gyeongdodat.domain.game_log.enums.GameAction;
import ctrlg.gyeongdodat.domain.game_log.repository.GameLogRedisRepository;
import ctrlg.gyeongdodat.domain.game_log.service.command.GameLogCreateCommand;
import ctrlg.gyeongdodat.global.exception.ErrorCode;
import ctrlg.gyeongdodat.global.exception.GlobalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GameLogRedisServiceTest {

    @InjectMocks
    private GameLogRedisService gameLogRedisService;

    @Mock
    private GameLogRedisRepository gameLogRedisRepository;

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("정상적으로 게임 로그를 생성한다")
        void createGameLog() {
            // given
            GameLogCreateCommand command = GameLogCreateCommand.builder()
                    .gameId("game-123")
                    .actorPlayerId("player-456")
                    .targetPlayerId("player-789")
                    .action(GameAction.ARREST)
                    .build();

            GameLogRedis savedLog = GameLogRedis.builder()
                    .id(1L)
                    .gameId("game-123")
                    .actorPlayerId("player-456")
                    .targetPlayerId("player-789")
                    .action(GameAction.ARREST)
                    .build();

            given(gameLogRedisRepository.save(any(GameLogRedis.class))).willReturn(savedLog);

            // when
            GameLogRedis result = gameLogRedisService.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getGameId()).isEqualTo("game-123");
            assertThat(result.getAction()).isEqualTo(GameAction.ARREST);
            verify(gameLogRedisRepository).save(any(GameLogRedis.class));
        }
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("ID로 게임 로그를 정상 조회한다")
        void findById() {
            // given
            Long logId = 1L;
            GameLogRedis log = GameLogRedis.builder()
                    .id(logId)
                    .gameId("game-123")
                    .action(GameAction.ARREST)
                    .build();

            given(gameLogRedisRepository.findById(logId)).willReturn(Optional.of(log));

            // when
            GameLogRedis result = gameLogRedisService.findById(logId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(logId);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 예외가 발생한다")
        void findByIdNotFound() {
            // given
            Long logId = 999L;
            given(gameLogRedisRepository.findById(logId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> gameLogRedisService.findById(logId))
                    .isInstanceOf(GlobalException.class)
                    .extracting(e -> ((GlobalException) e).getErrorCode())
                    .isEqualTo(ErrorCode.GAME_LOG_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("findByGameId 메서드")
    class FindByGameId {

        @Test
        @DisplayName("gameId로 게임 로그 목록을 조회한다")
        void findByGameId() {
            // given
            String gameId = "game-123";
            GameLogRedis log1 = GameLogRedis.builder().id(1L).gameId(gameId).build();
            GameLogRedis log2 = GameLogRedis.builder().id(2L).gameId(gameId).build();

            given(gameLogRedisRepository.findByGameId(gameId)).willReturn(List.of(log1, log2));

            // when
            List<GameLogRedis> result = gameLogRedisService.findByGameId(gameId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).extracting(GameLogRedis::getGameId)
                    .containsOnly(gameId);
        }
    }

    @Nested
    @DisplayName("findAll 메서드")
    class FindAll {

        @Test
        @DisplayName("전체 게임 로그 목록을 조회한다")
        void findAll() {
            // given
            GameLogRedis log1 = GameLogRedis.builder().id(1L).build();
            GameLogRedis log2 = GameLogRedis.builder().id(2L).build();

            given(gameLogRedisRepository.findAll()).willReturn(List.of(log1, log2));

            // when
            List<GameLogRedis> result = gameLogRedisService.findAll();

            // then
            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("delete 메서드")
    class Delete {

        @Test
        @DisplayName("정상적으로 게임 로그를 삭제한다")
        void deleteGameLog() {
            // given
            Long logId = 1L;
            given(gameLogRedisRepository.existsById(logId)).willReturn(true);

            // when
            gameLogRedisService.delete(logId);

            // then
            verify(gameLogRedisRepository).deleteById(logId);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 삭제하면 예외가 발생한다")
        void deleteGameLogNotFound() {
            // given
            Long logId = 999L;
            given(gameLogRedisRepository.existsById(logId)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> gameLogRedisService.delete(logId))
                    .isInstanceOf(GlobalException.class)
                    .extracting(e -> ((GlobalException) e).getErrorCode())
                    .isEqualTo(ErrorCode.GAME_LOG_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("deleteByGameId 메서드")
    class DeleteByGameId {

        @Test
        @DisplayName("gameId로 게임 로그들을 일괄 삭제한다")
        void deleteByGameId() {
            // given
            String gameId = "game-123";
            GameLogRedis log1 = GameLogRedis.builder().id(1L).gameId(gameId).build();
            GameLogRedis log2 = GameLogRedis.builder().id(2L).gameId(gameId).build();

            given(gameLogRedisRepository.findByGameId(gameId)).willReturn(List.of(log1, log2));

            // when
            gameLogRedisService.deleteByGameId(gameId);

            // then
            verify(gameLogRedisRepository).deleteAll(List.of(log1, log2));
        }
    }
}
