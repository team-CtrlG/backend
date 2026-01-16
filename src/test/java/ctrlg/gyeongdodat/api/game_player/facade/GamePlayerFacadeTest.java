package ctrlg.gyeongdodat.api.game_player.facade;

import ctrlg.gyeongdodat.domain.game_player.entity.GamePlayerRedis;
import ctrlg.gyeongdodat.domain.game_player.enums.PlayerStatus;
import ctrlg.gyeongdodat.domain.game_player.enums.Team;
import ctrlg.gyeongdodat.domain.game_player.service.GamePlayerPosRedisService;
import ctrlg.gyeongdodat.domain.game_player.service.GamePlayerRedisService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GamePlayerFacadeTest {

    @InjectMocks
    private GamePlayerFacade gamePlayerFacade;

    @Mock
    private GamePlayerRedisService playerService;

    @Mock
    private GamePlayerPosRedisService playerPosService;

    @Nested
    @DisplayName("arrestThief 메서드")
    class ArrestThief {

        @Test
        @DisplayName("경찰이 도둑을 성공적으로 체포한다")
        void arrestThiefSuccess() {
            // given
            String gameId = "game-123";
            String policePlayerId = "police-123";
            Integer thiefNumber = 1;

            GamePlayerRedis police = GamePlayerRedis.builder()
                    .id(policePlayerId)
                    .gameId(gameId)
                    .team(Team.POLICE)
                    .caughtCount(0)
                    .build();

            GamePlayerRedis thief = GamePlayerRedis.builder()
                    .id("thief-123")
                    .gameId(gameId)
                    .team(Team.THIEF)
                    .thiefNumber(thiefNumber)
                    .status(PlayerStatus.ACTIVE)
                    .build();

            GamePlayerRedis updatedThief = GamePlayerRedis.builder()
                    .id("thief-123")
                    .gameId(gameId)
                    .team(Team.THIEF)
                    .thiefNumber(thiefNumber)
                    .status(PlayerStatus.JAILED)
                    .build();

            GamePlayerRedis updatedPolice = GamePlayerRedis.builder()
                    .id(policePlayerId)
                    .gameId(gameId)
                    .team(Team.POLICE)
                    .caughtCount(1)
                    .build();

            given(playerService.findById(policePlayerId)).willReturn(police);
            given(playerService.findByGameIdAndThiefNumber(gameId, thiefNumber)).willReturn(thief);
            given(playerService.update(eq("thief-123"), any(GamePlayerUpdateCommand.class))).willReturn(updatedThief);
            given(playerService.update(eq(policePlayerId), any(GamePlayerUpdateCommand.class))).willReturn(updatedPolice);

            // when
            GamePlayerRedis result = gamePlayerFacade.arrestThief(gameId, policePlayerId, thiefNumber);

            // then
            assertThat(result.getStatus()).isEqualTo(PlayerStatus.JAILED);
            verify(playerService).update(eq("thief-123"), any(GamePlayerUpdateCommand.class));
            verify(playerService).update(eq(policePlayerId), any(GamePlayerUpdateCommand.class));
        }

        @Test
        @DisplayName("이미 수감된 도둑을 체포하려고 하면 예외가 발생한다")
        void arrestThiefAlreadyJailed() {
            // given
            String gameId = "game-123";
            String policePlayerId = "police-123";
            Integer thiefNumber = 1;

            GamePlayerRedis police = GamePlayerRedis.builder()
                    .id(policePlayerId)
                    .gameId(gameId)
                    .team(Team.POLICE)
                    .build();

            GamePlayerRedis jailedThief = GamePlayerRedis.builder()
                    .id("thief-123")
                    .gameId(gameId)
                    .team(Team.THIEF)
                    .thiefNumber(thiefNumber)
                    .status(PlayerStatus.JAILED)
                    .build();

            given(playerService.findById(policePlayerId)).willReturn(police);
            given(playerService.findByGameIdAndThiefNumber(gameId, thiefNumber)).willReturn(jailedThief);

            // when & then
            assertThatThrownBy(() -> gamePlayerFacade.arrestThief(gameId, policePlayerId, thiefNumber))
                    .isInstanceOf(GlobalException.class)
                    .extracting(e -> ((GlobalException) e).getErrorCode())
                    .isEqualTo(ErrorCode.ALREADY_JAILED);
        }

        @Test
        @DisplayName("경찰이 아닌 사용자가 체포하려고 하면 예외가 발생한다")
        void arrestThiefNotPolice() {
            // given
            String gameId = "game-123";
            String thiefPlayerId = "thief-456";
            Integer targetThiefNumber = 1;

            GamePlayerRedis notPolice = GamePlayerRedis.builder()
                    .id(thiefPlayerId)
                    .gameId(gameId)
                    .team(Team.THIEF)
                    .build();

            given(playerService.findById(thiefPlayerId)).willReturn(notPolice);

            // when & then
            assertThatThrownBy(() -> gamePlayerFacade.arrestThief(gameId, thiefPlayerId, targetThiefNumber))
                    .isInstanceOf(GlobalException.class)
                    .extracting(e -> ((GlobalException) e).getErrorCode())
                    .isEqualTo(ErrorCode.NOT_POLICE);
        }

        @Test
        @DisplayName("존재하지 않는 도둑 번호로 체포하려고 하면 예외가 발생한다")
        void arrestThiefThiefNotFound() {
            // given
            String gameId = "game-123";
            String policePlayerId = "police-123";
            Integer nonExistentThiefNumber = 999;

            GamePlayerRedis police = GamePlayerRedis.builder()
                    .id(policePlayerId)
                    .gameId(gameId)
                    .team(Team.POLICE)
                    .build();

            given(playerService.findById(policePlayerId)).willReturn(police);
            given(playerService.findByGameIdAndThiefNumber(gameId, nonExistentThiefNumber)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> gamePlayerFacade.arrestThief(gameId, policePlayerId, nonExistentThiefNumber))
                    .isInstanceOf(GlobalException.class)
                    .extracting(e -> ((GlobalException) e).getErrorCode())
                    .isEqualTo(ErrorCode.THIEF_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("rescueThief 메서드")
    class RescueThief {

        @Test
        @DisplayName("도둑이 수감된 동료를 성공적으로 구출한다")
        void rescueThiefSuccess() {
            // given
            String gameId = "game-123";
            String rescuerPlayerId = "rescuer-123";
            Integer targetThiefNumber = 1;

            GamePlayerRedis rescuer = GamePlayerRedis.builder()
                    .id(rescuerPlayerId)
                    .gameId(gameId)
                    .team(Team.THIEF)
                    .status(PlayerStatus.ACTIVE)
                    .escapeCount(0)
                    .build();

            GamePlayerRedis jailedThief = GamePlayerRedis.builder()
                    .id("jailed-123")
                    .gameId(gameId)
                    .team(Team.THIEF)
                    .thiefNumber(targetThiefNumber)
                    .status(PlayerStatus.JAILED)
                    .jailedJailNo(1)
                    .build();

            GamePlayerRedis rescuedThief = GamePlayerRedis.builder()
                    .id("jailed-123")
                    .gameId(gameId)
                    .team(Team.THIEF)
                    .thiefNumber(targetThiefNumber)
                    .status(PlayerStatus.ACTIVE)
                    .jailedJailNo(null)
                    .build();

            GamePlayerRedis updatedRescuer = GamePlayerRedis.builder()
                    .id(rescuerPlayerId)
                    .gameId(gameId)
                    .team(Team.THIEF)
                    .status(PlayerStatus.ACTIVE)
                    .escapeCount(1)
                    .build();

            given(playerService.findById(rescuerPlayerId)).willReturn(rescuer);
            given(playerService.findByGameIdAndThiefNumber(gameId, targetThiefNumber)).willReturn(jailedThief);
            given(playerService.update(eq("jailed-123"), any(GamePlayerUpdateCommand.class))).willReturn(rescuedThief);
            given(playerService.update(eq(rescuerPlayerId), any(GamePlayerUpdateCommand.class))).willReturn(updatedRescuer);

            // when
            GamePlayerRedis result = gamePlayerFacade.rescueThief(gameId, rescuerPlayerId, targetThiefNumber);

            // then
            assertThat(result.getStatus()).isEqualTo(PlayerStatus.ACTIVE);
            verify(playerService).update(eq("jailed-123"), any(GamePlayerUpdateCommand.class));
            verify(playerService).update(eq(rescuerPlayerId), any(GamePlayerUpdateCommand.class));
        }

        @Test
        @DisplayName("수감되지 않은 도둑을 구출하려고 하면 예외가 발생한다")
        void rescueThiefNotJailed() {
            // given
            String gameId = "game-123";
            String rescuerPlayerId = "rescuer-123";
            Integer targetThiefNumber = 1;

            GamePlayerRedis rescuer = GamePlayerRedis.builder()
                    .id(rescuerPlayerId)
                    .gameId(gameId)
                    .team(Team.THIEF)
                    .status(PlayerStatus.ACTIVE)
                    .build();

            GamePlayerRedis activeThief = GamePlayerRedis.builder()
                    .id("active-123")
                    .gameId(gameId)
                    .team(Team.THIEF)
                    .thiefNumber(targetThiefNumber)
                    .status(PlayerStatus.ACTIVE)
                    .build();

            given(playerService.findById(rescuerPlayerId)).willReturn(rescuer);
            given(playerService.findByGameIdAndThiefNumber(gameId, targetThiefNumber)).willReturn(activeThief);

            // when & then
            assertThatThrownBy(() -> gamePlayerFacade.rescueThief(gameId, rescuerPlayerId, targetThiefNumber))
                    .isInstanceOf(GlobalException.class)
                    .extracting(e -> ((GlobalException) e).getErrorCode())
                    .isEqualTo(ErrorCode.NOT_JAILED);
        }

        @Test
        @DisplayName("경찰이 구출하려고 하면 예외가 발생한다")
        void rescueThiefNotThief() {
            // given
            String gameId = "game-123";
            String policePlayerId = "police-123";
            Integer targetThiefNumber = 1;

            GamePlayerRedis police = GamePlayerRedis.builder()
                    .id(policePlayerId)
                    .gameId(gameId)
                    .team(Team.POLICE)
                    .build();

            given(playerService.findById(policePlayerId)).willReturn(police);

            // when & then
            assertThatThrownBy(() -> gamePlayerFacade.rescueThief(gameId, policePlayerId, targetThiefNumber))
                    .isInstanceOf(GlobalException.class)
                    .extracting(e -> ((GlobalException) e).getErrorCode())
                    .isEqualTo(ErrorCode.NOT_THIEF);
        }

        @Test
        @DisplayName("수감 상태인 도둑이 구출하려고 하면 예외가 발생한다")
        void rescueThiefRescuerIsJailed() {
            // given
            String gameId = "game-123";
            String jailedRescuerPlayerId = "jailed-rescuer-123";
            Integer targetThiefNumber = 1;

            GamePlayerRedis jailedRescuer = GamePlayerRedis.builder()
                    .id(jailedRescuerPlayerId)
                    .gameId(gameId)
                    .team(Team.THIEF)
                    .status(PlayerStatus.JAILED)
                    .build();

            given(playerService.findById(jailedRescuerPlayerId)).willReturn(jailedRescuer);

            // when & then
            assertThatThrownBy(() -> gamePlayerFacade.rescueThief(gameId, jailedRescuerPlayerId, targetThiefNumber))
                    .isInstanceOf(GlobalException.class)
                    .extracting(e -> ((GlobalException) e).getErrorCode())
                    .isEqualTo(ErrorCode.RESCUER_IS_JAILED);
        }

        @Test
        @DisplayName("존재하지 않는 도둑 번호로 구출하려고 하면 예외가 발생한다")
        void rescueThiefThiefNotFound() {
            // given
            String gameId = "game-123";
            String rescuerPlayerId = "rescuer-123";
            Integer nonExistentThiefNumber = 999;

            GamePlayerRedis rescuer = GamePlayerRedis.builder()
                    .id(rescuerPlayerId)
                    .gameId(gameId)
                    .team(Team.THIEF)
                    .status(PlayerStatus.ACTIVE)
                    .build();

            given(playerService.findById(rescuerPlayerId)).willReturn(rescuer);
            given(playerService.findByGameIdAndThiefNumber(gameId, nonExistentThiefNumber)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> gamePlayerFacade.rescueThief(gameId, rescuerPlayerId, nonExistentThiefNumber))
                    .isInstanceOf(GlobalException.class)
                    .extracting(e -> ((GlobalException) e).getErrorCode())
                    .isEqualTo(ErrorCode.THIEF_NOT_FOUND);
        }
    }
}
