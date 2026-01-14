package ctrlg.gyeongdodat.domain.game_player.service;

import ctrlg.gyeongdodat.domain.game_player.entity.GamePlayerRedis;
import ctrlg.gyeongdodat.domain.game_player.enums.ConnectionState;
import ctrlg.gyeongdodat.domain.game_player.enums.PlayerRole;
import ctrlg.gyeongdodat.domain.game_player.enums.PlayerStatus;
import ctrlg.gyeongdodat.domain.game_player.enums.Team;
import ctrlg.gyeongdodat.domain.game_player.repository.GamePlayerRedisRepository;
import ctrlg.gyeongdodat.domain.game_player.service.command.GamePlayerCreateCommand;
import ctrlg.gyeongdodat.domain.game_player.service.command.GamePlayerUpdateCommand;
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
class GamePlayerRedisServiceTest {

    @InjectMocks
    private GamePlayerRedisService gamePlayerRedisService;

    @Mock
    private GamePlayerRedisRepository gamePlayerRedisRepository;

    @Nested
    @DisplayName("create 메서드")
    class Create {

        @Test
        @DisplayName("정상적으로 게임 플레이어를 생성한다")
        void createGamePlayer() {
            // given
            GamePlayerCreateCommand command = GamePlayerCreateCommand.builder()
                    .gameId("game-123")
                    .userId("user-456")
                    .role(PlayerRole.PLAYER)
                    .team(Team.POLICE)
                    .build();

            GamePlayerRedis savedPlayer = GamePlayerRedis.builder()
                    .id("player-789")
                    .gameId("game-123")
                    .userId("user-456")
                    .role(PlayerRole.PLAYER)
                    .team(Team.POLICE)
                    .build();

            given(gamePlayerRedisRepository.save(any(GamePlayerRedis.class))).willReturn(savedPlayer);

            // when
            GamePlayerRedis result = gamePlayerRedisService.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getGameId()).isEqualTo("game-123");
            assertThat(result.getUserId()).isEqualTo("user-456");
            verify(gamePlayerRedisRepository).save(any(GamePlayerRedis.class));
        }
    }

    @Nested
    @DisplayName("findById 메서드")
    class FindById {

        @Test
        @DisplayName("ID로 게임 플레이어를 정상 조회한다")
        void findById() {
            // given
            String playerId = "player-123";
            GamePlayerRedis player = GamePlayerRedis.builder()
                    .id(playerId)
                    .gameId("game-456")
                    .build();

            given(gamePlayerRedisRepository.findById(playerId)).willReturn(Optional.of(player));

            // when
            GamePlayerRedis result = gamePlayerRedisService.findById(playerId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(playerId);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 예외가 발생한다")
        void findByIdNotFound() {
            // given
            String playerId = "non-existent-id";
            given(gamePlayerRedisRepository.findById(playerId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> gamePlayerRedisService.findById(playerId))
                    .isInstanceOf(GlobalException.class)
                    .extracting(e -> ((GlobalException) e).getErrorCode())
                    .isEqualTo(ErrorCode.GAME_PLAYER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("findByGameId 메서드")
    class FindByGameId {

        @Test
        @DisplayName("gameId로 게임 플레이어 목록을 조회한다")
        void findByGameId() {
            // given
            String gameId = "game-123";
            GamePlayerRedis player1 = GamePlayerRedis.builder().id("player-1").gameId(gameId).build();
            GamePlayerRedis player2 = GamePlayerRedis.builder().id("player-2").gameId(gameId).build();

            given(gamePlayerRedisRepository.findByGameId(gameId)).willReturn(List.of(player1, player2));

            // when
            List<GamePlayerRedis> result = gamePlayerRedisService.findByGameId(gameId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).extracting(GamePlayerRedis::getGameId)
                    .containsOnly(gameId);
        }
    }

    @Nested
    @DisplayName("findAll 메서드")
    class FindAll {

        @Test
        @DisplayName("전체 게임 플레이어 목록을 조회한다")
        void findAll() {
            // given
            GamePlayerRedis player1 = GamePlayerRedis.builder().id("player-1").build();
            GamePlayerRedis player2 = GamePlayerRedis.builder().id("player-2").build();

            given(gamePlayerRedisRepository.findAll()).willReturn(List.of(player1, player2));

            // when
            List<GamePlayerRedis> result = gamePlayerRedisService.findAll();

            // then
            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("update 메서드")
    class Update {

        @Test
        @DisplayName("정상적으로 게임 플레이어를 업데이트한다")
        void updateGamePlayer() {
            // given
            String playerId = "player-123";
            GamePlayerRedis player = GamePlayerRedis.builder()
                    .id(playerId)
                    .caughtCount(0)
                    .connectionState(ConnectionState.UNKNOWN)
                    .build();

            GamePlayerUpdateCommand command = GamePlayerUpdateCommand.builder()
                    .caughtCount(5)
                    .connectionState(ConnectionState.CONNECTED)
                    .status(PlayerStatus.JAILED)
                    .build();

            given(gamePlayerRedisRepository.findById(playerId)).willReturn(Optional.of(player));
            given(gamePlayerRedisRepository.save(any(GamePlayerRedis.class))).willReturn(player);

            // when
            GamePlayerRedis result = gamePlayerRedisService.update(playerId, command);

            // then
            assertThat(result.getCaughtCount()).isEqualTo(5);
            assertThat(result.getConnectionState()).isEqualTo(ConnectionState.CONNECTED);
            verify(gamePlayerRedisRepository).save(player);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 업데이트하면 예외가 발생한다")
        void updateGamePlayerNotFound() {
            // given
            String playerId = "non-existent-id";
            GamePlayerUpdateCommand command = GamePlayerUpdateCommand.builder()
                    .caughtCount(5)
                    .build();

            given(gamePlayerRedisRepository.findById(playerId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> gamePlayerRedisService.update(playerId, command))
                    .isInstanceOf(GlobalException.class)
                    .extracting(e -> ((GlobalException) e).getErrorCode())
                    .isEqualTo(ErrorCode.GAME_PLAYER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("delete 메서드")
    class Delete {

        @Test
        @DisplayName("정상적으로 게임 플레이어를 삭제한다")
        void deleteGamePlayer() {
            // given
            String playerId = "player-123";
            given(gamePlayerRedisRepository.existsById(playerId)).willReturn(true);

            // when
            gamePlayerRedisService.delete(playerId);

            // then
            verify(gamePlayerRedisRepository).deleteById(playerId);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 삭제하면 예외가 발생한다")
        void deleteGamePlayerNotFound() {
            // given
            String playerId = "non-existent-id";
            given(gamePlayerRedisRepository.existsById(playerId)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> gamePlayerRedisService.delete(playerId))
                    .isInstanceOf(GlobalException.class)
                    .extracting(e -> ((GlobalException) e).getErrorCode())
                    .isEqualTo(ErrorCode.GAME_PLAYER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("deleteByGameId 메서드")
    class DeleteByGameId {

        @Test
        @DisplayName("gameId로 게임 플레이어들을 일괄 삭제한다")
        void deleteByGameId() {
            // given
            String gameId = "game-123";
            GamePlayerRedis player1 = GamePlayerRedis.builder().id("player-1").gameId(gameId).build();
            GamePlayerRedis player2 = GamePlayerRedis.builder().id("player-2").gameId(gameId).build();

            given(gamePlayerRedisRepository.findByGameId(gameId)).willReturn(List.of(player1, player2));

            // when
            gamePlayerRedisService.deleteByGameId(gameId);

            // then
            verify(gamePlayerRedisRepository).deleteAll(List.of(player1, player2));
        }
    }
}
