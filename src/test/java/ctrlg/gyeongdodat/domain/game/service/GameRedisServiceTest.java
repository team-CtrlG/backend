package ctrlg.gyeongdodat.domain.game.service;

import ctrlg.gyeongdodat.domain.game.entity.GameRedis;
import ctrlg.gyeongdodat.domain.game.enums.WinTeam;
import ctrlg.gyeongdodat.domain.game.repository.GameRedisRepository;
import ctrlg.gyeongdodat.domain.game.service.command.GameCreateCommand;
import ctrlg.gyeongdodat.domain.game.service.command.GameUpdateCommand;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GameRedisServiceTest {

    @InjectMocks
    private GameRedisService gameRedisService;

    @Mock
    private GameRedisRepository gameRedisRepository;

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("정상적으로 게임을 생성한다")
        void createGame() {
            // given
            GameCreateCommand command = GameCreateCommand.builder()
                    .gameTimeSec(3600)
                    .hideTimeSec(300)
                    .jailLat(new BigDecimal("37.5665"))
                    .jailLng(new BigDecimal("126.9780"))
                    .jailImage("jail.png")
                    .rulesJson("{\"rule\": \"test\"}")
                    .build();

            GameRedis savedGame = GameRedis.builder()
                    .id("game-123")
                    .gameTimeSec(3600)
                    .hideTimeSec(300)
                    .jailLat(new BigDecimal("37.5665"))
                    .jailLng(new BigDecimal("126.9780"))
                    .jailImage("jail.png")
                    .rulesJson("{\"rule\": \"test\"}")
                    .build();

            given(gameRedisRepository.save(any(GameRedis.class))).willReturn(savedGame);

            // when
            GameRedis result = gameRedisService.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getGameTimeSec()).isEqualTo(3600);
            assertThat(result.getHideTimeSec()).isEqualTo(300);
            verify(gameRedisRepository).save(any(GameRedis.class));
        }
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("ID로 게임을 정상 조회한다")
        void findById() {
            // given
            String gameId = "game-123";
            GameRedis game = GameRedis.builder()
                    .id(gameId)
                    .gameTimeSec(3600)
                    .build();

            given(gameRedisRepository.findById(gameId)).willReturn(Optional.of(game));

            // when
            GameRedis result = gameRedisService.findById(gameId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(gameId);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 예외가 발생한다")
        void findByIdNotFound() {
            // given
            String gameId = "non-existent-id";
            given(gameRedisRepository.findById(gameId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> gameRedisService.findById(gameId))
                    .isInstanceOf(GlobalException.class)
                    .extracting(e -> ((GlobalException) e).getErrorCode())
                    .isEqualTo(ErrorCode.GAME_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("findByIdOrNull 메서드")
    class FindByIdOrNull {

        @Test
        @DisplayName("ID로 게임을 정상 조회한다")
        void findByIdOrNull() {
            // given
            String gameId = "game-123";
            GameRedis game = GameRedis.builder()
                    .id(gameId)
                    .gameTimeSec(3600)
                    .build();

            given(gameRedisRepository.findById(gameId)).willReturn(Optional.of(game));

            // when
            GameRedis result = gameRedisService.findByIdOrNull(gameId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(gameId);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 null을 반환한다")
        void findByIdOrNullReturnsNull() {
            // given
            String gameId = "non-existent-id";
            given(gameRedisRepository.findById(gameId)).willReturn(Optional.empty());

            // when
            GameRedis result = gameRedisService.findByIdOrNull(gameId);

            // then
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("findAll 메서드")
    class FindAll {

        @Test
        @DisplayName("전체 게임 목록을 조회한다")
        void findAll() {
            // given
            GameRedis game1 = GameRedis.builder().id("game-1").build();
            GameRedis game2 = GameRedis.builder().id("game-2").build();

            given(gameRedisRepository.findAll()).willReturn(List.of(game1, game2));

            // when
            List<GameRedis> result = gameRedisService.findAll();

            // then
            assertThat(result).hasSize(2);
            assertThat(result).extracting(GameRedis::getId)
                    .containsExactlyInAnyOrder("game-1", "game-2");
        }
    }

    @Nested
    @DisplayName("update 메서드")
    class Update {

        @Test
        @DisplayName("정상적으로 게임을 업데이트한다")
        void updateGame() {
            // given
            String gameId = "game-123";
            GameRedis game = GameRedis.builder()
                    .id(gameId)
                    .gameTimeSec(3600)
                    .winTeam(WinTeam.UNKNOWN)
                    .build();

            GameUpdateCommand command = GameUpdateCommand.builder()
                    .winTeam(WinTeam.POLICE)
                    .startedAt(LocalDateTime.now())
                    .build();

            given(gameRedisRepository.findById(gameId)).willReturn(Optional.of(game));
            given(gameRedisRepository.save(any(GameRedis.class))).willReturn(game);

            // when
            GameRedis result = gameRedisService.update(gameId, command);

            // then
            assertThat(result.getWinTeam()).isEqualTo(WinTeam.POLICE);
            verify(gameRedisRepository).save(game);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 업데이트하면 예외가 발생한다")
        void updateGameNotFound() {
            // given
            String gameId = "non-existent-id";
            GameUpdateCommand command = GameUpdateCommand.builder()
                    .winTeam(WinTeam.POLICE)
                    .build();

            given(gameRedisRepository.findById(gameId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> gameRedisService.update(gameId, command))
                    .isInstanceOf(GlobalException.class)
                    .extracting(e -> ((GlobalException) e).getErrorCode())
                    .isEqualTo(ErrorCode.GAME_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("delete 메서드")
    class Delete {

        @Test
        @DisplayName("정상적으로 게임을 삭제한다")
        void deleteGame() {
            // given
            String gameId = "game-123";
            given(gameRedisRepository.existsById(gameId)).willReturn(true);

            // when
            gameRedisService.delete(gameId);

            // then
            verify(gameRedisRepository).deleteById(gameId);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 삭제하면 예외가 발생한다")
        void deleteGameNotFound() {
            // given
            String gameId = "non-existent-id";
            given(gameRedisRepository.existsById(gameId)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> gameRedisService.delete(gameId))
                    .isInstanceOf(GlobalException.class)
                    .extracting(e -> ((GlobalException) e).getErrorCode())
                    .isEqualTo(ErrorCode.GAME_NOT_FOUND);
        }
    }
}
