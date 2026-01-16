package ctrlg.gyeongdodat.api.game.facade;

import ctrlg.gyeongdodat.domain.game.entity.GameRedis;
import ctrlg.gyeongdodat.domain.game.enums.WinTeam;
import ctrlg.gyeongdodat.domain.game.service.GameRedisService;
import ctrlg.gyeongdodat.domain.game.service.command.GameUpdateCommand;
import ctrlg.gyeongdodat.domain.game_log.service.GameLogRedisService;
import ctrlg.gyeongdodat.domain.game_player.entity.GamePlayerRedis;
import ctrlg.gyeongdodat.domain.game_player.enums.PlayerStatus;
import ctrlg.gyeongdodat.domain.game_player.enums.Team;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GameFacadeTest {

    @InjectMocks
    private GameFacade gameFacade;

    @Mock
    private GameRedisService gameService;

    @Mock
    private GamePlayerRedisService playerService;

    @Mock
    private GameLogRedisService logService;

    @Nested
    @DisplayName("checkAttendance 메서드")
    class CheckAttendance {

        @Test
        @DisplayName("정상적으로 출석 처리한다")
        void checkAttendanceSuccess() {
            // given
            String gameId = "game-123";
            String playerId = "player-123";
            String code = "1234";

            GameRedis game = GameRedis.builder()
                    .id(gameId)
                    .attendanceCode(code)
                    .build();

            GamePlayerRedis player = GamePlayerRedis.builder()
                    .id(playerId)
                    .gameId(gameId)
                    .team(Team.THIEF)
                    .attendanceYn(false)
                    .build();

            GamePlayerRedis updatedPlayer = GamePlayerRedis.builder()
                    .id(playerId)
                    .gameId(gameId)
                    .team(Team.THIEF)
                    .attendanceYn(true)
                    .build();

            given(gameService.findById(gameId)).willReturn(game);
            given(playerService.findById(playerId)).willReturn(player);
            given(playerService.update(eq(playerId), any(GamePlayerUpdateCommand.class))).willReturn(updatedPlayer);

            // when
            GamePlayerRedis result = gameFacade.checkAttendance(gameId, playerId, code);

            // then
            assertThat(result.isAttendanceYn()).isTrue();
            verify(playerService).update(eq(playerId), any(GamePlayerUpdateCommand.class));
        }

        @Test
        @DisplayName("잘못된 출석코드로 출석하면 예외가 발생한다")
        void checkAttendanceInvalidCode() {
            // given
            String gameId = "game-123";
            String playerId = "player-123";
            String correctCode = "1234";
            String wrongCode = "9999";

            GameRedis game = GameRedis.builder()
                    .id(gameId)
                    .attendanceCode(correctCode)
                    .build();

            given(gameService.findById(gameId)).willReturn(game);

            // when & then
            assertThatThrownBy(() -> gameFacade.checkAttendance(gameId, playerId, wrongCode))
                    .isInstanceOf(GlobalException.class)
                    .extracting(e -> ((GlobalException) e).getErrorCode())
                    .isEqualTo(ErrorCode.INVALID_ATTENDANCE_CODE);
        }

        @Test
        @DisplayName("이미 출석한 플레이어가 다시 출석하면 예외가 발생한다")
        void checkAttendanceAlreadyAttended() {
            // given
            String gameId = "game-123";
            String playerId = "player-123";
            String code = "1234";

            GameRedis game = GameRedis.builder()
                    .id(gameId)
                    .attendanceCode(code)
                    .build();

            GamePlayerRedis player = GamePlayerRedis.builder()
                    .id(playerId)
                    .gameId(gameId)
                    .team(Team.THIEF)
                    .attendanceYn(true) // 이미 출석함
                    .build();

            given(gameService.findById(gameId)).willReturn(game);
            given(playerService.findById(playerId)).willReturn(player);

            // when & then
            assertThatThrownBy(() -> gameFacade.checkAttendance(gameId, playerId, code))
                    .isInstanceOf(GlobalException.class)
                    .extracting(e -> ((GlobalException) e).getErrorCode())
                    .isEqualTo(ErrorCode.ALREADY_ATTENDED);
        }
    }

    @Nested
    @DisplayName("getRemainingTimeSec 메서드")
    class GetRemainingTimeSec {

        @Test
        @DisplayName("게임 진행 중일 때 남은 시간을 계산한다")
        void getRemainingTimeSecDuringGame() {
            // given
            String gameId = "game-123";
            LocalDateTime startedAt = LocalDateTime.now().minusSeconds(60); // 60초 전 시작
            Integer gameTimeSec = 300; // 5분 게임

            GameRedis game = GameRedis.builder()
                    .id(gameId)
                    .gameTimeSec(gameTimeSec)
                    .startedAt(startedAt)
                    .build();

            given(gameService.findById(gameId)).willReturn(game);

            // when
            Integer result = gameFacade.getRemainingTimeSec(gameId);

            // then
            // 300 - 60 = 240초 근처 (테스트 실행 시간에 따라 약간의 오차 있음)
            assertThat(result).isBetween(238, 242);
        }

        @Test
        @DisplayName("게임 시작 전에는 전체 시간을 반환한다")
        void getRemainingTimeSecBeforeStart() {
            // given
            String gameId = "game-123";
            Integer gameTimeSec = 300;

            GameRedis game = GameRedis.builder()
                    .id(gameId)
                    .gameTimeSec(gameTimeSec)
                    .startedAt(null) // 시작 안함
                    .build();

            given(gameService.findById(gameId)).willReturn(game);

            // when
            Integer result = gameFacade.getRemainingTimeSec(gameId);

            // then
            assertThat(result).isEqualTo(300);
        }

        @Test
        @DisplayName("게임 종료 후에는 0을 반환한다")
        void getRemainingTimeSecAfterEnd() {
            // given
            String gameId = "game-123";
            LocalDateTime startedAt = LocalDateTime.now().minusSeconds(400); // 400초 전 시작
            Integer gameTimeSec = 300; // 5분 게임 (이미 종료됨)

            GameRedis game = GameRedis.builder()
                    .id(gameId)
                    .gameTimeSec(gameTimeSec)
                    .startedAt(startedAt)
                    .endedAt(LocalDateTime.now().minusSeconds(100)) // 100초 전 종료
                    .build();

            given(gameService.findById(gameId)).willReturn(game);

            // when
            Integer result = gameFacade.getRemainingTimeSec(gameId);

            // then
            assertThat(result).isEqualTo(0);
        }

        @Test
        @DisplayName("남은 시간이 음수일 경우 0을 반환한다")
        void getRemainingTimeSecNegativeReturnsZero() {
            // given
            String gameId = "game-123";
            LocalDateTime startedAt = LocalDateTime.now().minusSeconds(400); // 400초 전 시작
            Integer gameTimeSec = 300; // 5분 게임

            GameRedis game = GameRedis.builder()
                    .id(gameId)
                    .gameTimeSec(gameTimeSec)
                    .startedAt(startedAt)
                    .build();

            given(gameService.findById(gameId)).willReturn(game);

            // when
            Integer result = gameFacade.getRemainingTimeSec(gameId);

            // then
            assertThat(result).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("endGame 메서드")
    class EndGame {

        @Test
        @DisplayName("정상적으로 게임을 종료하고 endedAt을 설정한다")
        void endGameSuccess() {
            // given
            String gameId = "game-123";

            GameRedis game = GameRedis.builder()
                    .id(gameId)
                    .gameTimeSec(300)
                    .startedAt(LocalDateTime.now().minusSeconds(300))
                    .build();

            // 모든 도둑이 수감됨 - 경찰 승리
            GamePlayerRedis thief1 = GamePlayerRedis.builder()
                    .id("thief-1")
                    .gameId(gameId)
                    .team(Team.THIEF)
                    .status(PlayerStatus.JAILED)
                    .build();

            GamePlayerRedis police1 = GamePlayerRedis.builder()
                    .id("police-1")
                    .gameId(gameId)
                    .team(Team.POLICE)
                    .status(PlayerStatus.ACTIVE)
                    .build();

            GameRedis endedGame = GameRedis.builder()
                    .id(gameId)
                    .gameTimeSec(300)
                    .startedAt(LocalDateTime.now().minusSeconds(300))
                    .endedAt(LocalDateTime.now())
                    .winTeam(WinTeam.POLICE)
                    .build();

            given(gameService.findById(gameId)).willReturn(game);
            given(playerService.findByGameId(gameId)).willReturn(List.of(thief1, police1));
            given(gameService.update(eq(gameId), any(GameUpdateCommand.class))).willReturn(endedGame);

            // when
            GameRedis result = gameFacade.endGame(gameId);

            // then
            assertThat(result.getEndedAt()).isNotNull();
            assertThat(result.getWinTeam()).isEqualTo(WinTeam.POLICE);
            verify(gameService).update(eq(gameId), any(GameUpdateCommand.class));
        }

        @Test
        @DisplayName("모든 도둑이 수감되면 경찰이 승리한다")
        void endGamePoliceWins() {
            // given
            String gameId = "game-123";

            GameRedis game = GameRedis.builder()
                    .id(gameId)
                    .gameTimeSec(300)
                    .startedAt(LocalDateTime.now().minusSeconds(60))
                    .build();

            GamePlayerRedis thief1 = GamePlayerRedis.builder()
                    .id("thief-1")
                    .gameId(gameId)
                    .team(Team.THIEF)
                    .status(PlayerStatus.JAILED)
                    .build();

            GamePlayerRedis thief2 = GamePlayerRedis.builder()
                    .id("thief-2")
                    .gameId(gameId)
                    .team(Team.THIEF)
                    .status(PlayerStatus.JAILED)
                    .build();

            GamePlayerRedis police1 = GamePlayerRedis.builder()
                    .id("police-1")
                    .gameId(gameId)
                    .team(Team.POLICE)
                    .status(PlayerStatus.ACTIVE)
                    .build();

            GameRedis endedGame = GameRedis.builder()
                    .id(gameId)
                    .endedAt(LocalDateTime.now())
                    .winTeam(WinTeam.POLICE)
                    .build();

            given(gameService.findById(gameId)).willReturn(game);
            given(playerService.findByGameId(gameId)).willReturn(List.of(thief1, thief2, police1));
            given(gameService.update(eq(gameId), any(GameUpdateCommand.class))).willReturn(endedGame);

            // when
            GameRedis result = gameFacade.endGame(gameId);

            // then
            assertThat(result.getWinTeam()).isEqualTo(WinTeam.POLICE);
        }

        @Test
        @DisplayName("시간 종료 시 도둑이 생존하면 도둑이 승리한다")
        void endGameThiefWins() {
            // given
            String gameId = "game-123";

            GameRedis game = GameRedis.builder()
                    .id(gameId)
                    .gameTimeSec(300)
                    .startedAt(LocalDateTime.now().minusSeconds(400)) // 시간 초과
                    .build();

            GamePlayerRedis thief1 = GamePlayerRedis.builder()
                    .id("thief-1")
                    .gameId(gameId)
                    .team(Team.THIEF)
                    .status(PlayerStatus.ACTIVE) // 생존
                    .build();

            GamePlayerRedis thief2 = GamePlayerRedis.builder()
                    .id("thief-2")
                    .gameId(gameId)
                    .team(Team.THIEF)
                    .status(PlayerStatus.JAILED) // 수감
                    .build();

            GamePlayerRedis police1 = GamePlayerRedis.builder()
                    .id("police-1")
                    .gameId(gameId)
                    .team(Team.POLICE)
                    .status(PlayerStatus.ACTIVE)
                    .build();

            GameRedis endedGame = GameRedis.builder()
                    .id(gameId)
                    .endedAt(LocalDateTime.now())
                    .winTeam(WinTeam.THIEF)
                    .build();

            given(gameService.findById(gameId)).willReturn(game);
            given(playerService.findByGameId(gameId)).willReturn(List.of(thief1, thief2, police1));
            given(gameService.update(eq(gameId), any(GameUpdateCommand.class))).willReturn(endedGame);

            // when
            GameRedis result = gameFacade.endGame(gameId);

            // then
            assertThat(result.getWinTeam()).isEqualTo(WinTeam.THIEF);
        }

        @Test
        @DisplayName("도둑이 없으면 무승부가 된다")
        void endGameDrawNoThieves() {
            // given
            String gameId = "game-123";

            GameRedis game = GameRedis.builder()
                    .id(gameId)
                    .gameTimeSec(300)
                    .startedAt(LocalDateTime.now().minusSeconds(400))
                    .build();

            GamePlayerRedis police1 = GamePlayerRedis.builder()
                    .id("police-1")
                    .gameId(gameId)
                    .team(Team.POLICE)
                    .status(PlayerStatus.ACTIVE)
                    .build();

            GameRedis endedGame = GameRedis.builder()
                    .id(gameId)
                    .endedAt(LocalDateTime.now())
                    .winTeam(WinTeam.DRAW)
                    .build();

            given(gameService.findById(gameId)).willReturn(game);
            given(playerService.findByGameId(gameId)).willReturn(List.of(police1));
            given(gameService.update(eq(gameId), any(GameUpdateCommand.class))).willReturn(endedGame);

            // when
            GameRedis result = gameFacade.endGame(gameId);

            // then
            assertThat(result.getWinTeam()).isEqualTo(WinTeam.DRAW);
        }
    }
}
