package ctrlg.gyeongdodat.domain.game.service;

import ctrlg.gyeongdodat.api.game_player.dto.PlayerPositionResponse;
import ctrlg.gyeongdodat.domain.game.entity.GameRedis;
import ctrlg.gyeongdodat.domain.game_player.entity.GamePlayerPosRedis;
import ctrlg.gyeongdodat.domain.game_player.entity.GamePlayerRedis;
import ctrlg.gyeongdodat.domain.game_player.enums.PlayerStatus;
import ctrlg.gyeongdodat.domain.game_player.enums.Team;
import ctrlg.gyeongdodat.domain.game_player.service.GamePlayerPosRedisService;
import ctrlg.gyeongdodat.domain.game_player.service.GamePlayerRedisService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class GameSchedulerServiceTest {

	@InjectMocks
	private GameSchedulerService gameSchedulerService;

	@Mock
	private GameRedisService gameService;

	@Mock
	private GamePlayerRedisService playerService;

	@Mock
	private GamePlayerPosRedisService playerPosService;

	@Mock
	private SimpMessagingTemplate messagingTemplate;

	@Nested
	@DisplayName("broadcastGameStatus 메서드")
	class BroadcastGameStatus {

		@Test
		@DisplayName("진행 중인 게임에만 브로드캐스트한다")
		void broadcastOnlyActiveGames() {
			// given
			GameRedis activeGame = GameRedis.builder()
					.id("active-game")
					.startedAt(LocalDateTime.now().minusMinutes(5))
					.endedAt(null)
					.build();

			GameRedis notStartedGame = GameRedis.builder()
					.id("not-started-game")
					.startedAt(null)
					.endedAt(null)
					.build();

			GameRedis endedGame = GameRedis.builder()
					.id("ended-game")
					.startedAt(LocalDateTime.now().minusMinutes(10))
					.endedAt(LocalDateTime.now().minusMinutes(1))
					.build();

			given(gameService.findAll()).willReturn(List.of(activeGame, notStartedGame, endedGame));
			given(playerService.findByGameId("active-game")).willReturn(List.of());

			// when
			gameSchedulerService.broadcastGameStatus();

			// then
			// broadcastPlayerPositions와 broadcastJailStatus 모두에서 호출되므로 2번 호출됨
			verify(playerService, times(2)).findByGameId("active-game");
			verify(playerService, never()).findByGameId("not-started-game");
			verify(playerService, never()).findByGameId("ended-game");
		}

		@Test
		@DisplayName("게임이 없으면 브로드캐스트하지 않는다")
		void noGamesNoBroadcast() {
			// given
			given(gameService.findAll()).willReturn(List.of());

			// when
			gameSchedulerService.broadcastGameStatus();

			// then
			verifyNoInteractions(messagingTemplate);
		}
	}

	@Nested
	@DisplayName("플레이어 위치 브로드캐스트")
	class BroadcastPlayerPositions {

		@Test
		@DisplayName("모든 플레이어 위치 정보를 브로드캐스트한다")
		void broadcastAllPlayerPositions() {
			// given
			String gameId = "game-123";

			GameRedis activeGame = GameRedis.builder()
					.id(gameId)
					.startedAt(LocalDateTime.now().minusMinutes(5))
					.endedAt(null)
					.build();

			GamePlayerRedis player1 = GamePlayerRedis.builder()
					.id("player-1")
					.gameId(gameId)
					.team(Team.POLICE)
					.status(PlayerStatus.ACTIVE)
					.build();

			GamePlayerRedis player2 = GamePlayerRedis.builder()
					.id("player-2")
					.gameId(gameId)
					.team(Team.THIEF)
					.thiefNumber(1)
					.status(PlayerStatus.ACTIVE)
					.build();

			GamePlayerPosRedis pos1 = GamePlayerPosRedis.builder()
					.gamePlayerId("player-1")
					.lat(new BigDecimal("37.5665"))
					.lng(new BigDecimal("126.9780"))
					.build();

			GamePlayerPosRedis pos2 = GamePlayerPosRedis.builder()
					.gamePlayerId("player-2")
					.lat(new BigDecimal("37.5700"))
					.lng(new BigDecimal("126.9800"))
					.build();

			given(gameService.findAll()).willReturn(List.of(activeGame));
			given(playerService.findByGameId(gameId)).willReturn(List.of(player1, player2));
			given(playerPosService.findByGamePlayerId("player-1")).willReturn(pos1);
			given(playerPosService.findByGamePlayerId("player-2")).willReturn(pos2);

			// when
			gameSchedulerService.broadcastGameStatus();

			// then
			@SuppressWarnings("unchecked")
			ArgumentCaptor<List<PlayerPositionResponse>> positionsCaptor = ArgumentCaptor.forClass(List.class);
			verify(messagingTemplate).convertAndSend(eq("/send/game/" + gameId + "/positions"), positionsCaptor.capture());

			List<PlayerPositionResponse> positions = positionsCaptor.getValue();
			assertThat(positions).hasSize(2);
		}
	}

	@Nested
	@DisplayName("감옥 현황 브로드캐스트")
	class BroadcastJailStatus {

		@Test
		@DisplayName("수감된 플레이어만 브로드캐스트한다")
		void broadcastOnlyJailedPlayers() {
			// given
			String gameId = "game-123";

			GameRedis activeGame = GameRedis.builder()
					.id(gameId)
					.startedAt(LocalDateTime.now().minusMinutes(5))
					.endedAt(null)
					.build();

			GamePlayerRedis activePlayer = GamePlayerRedis.builder()
					.id("active-player")
					.gameId(gameId)
					.team(Team.THIEF)
					.status(PlayerStatus.ACTIVE)
					.build();

			GamePlayerRedis jailedPlayer = GamePlayerRedis.builder()
					.id("jailed-player")
					.gameId(gameId)
					.team(Team.THIEF)
					.status(PlayerStatus.JAILED)
					.build();

			GamePlayerPosRedis activePos = GamePlayerPosRedis.builder()
					.gamePlayerId("active-player")
					.lat(BigDecimal.ZERO)
					.lng(BigDecimal.ZERO)
					.build();

			GamePlayerPosRedis jailedPos = GamePlayerPosRedis.builder()
					.gamePlayerId("jailed-player")
					.lat(BigDecimal.ZERO)
					.lng(BigDecimal.ZERO)
					.build();

			given(gameService.findAll()).willReturn(List.of(activeGame));
			given(playerService.findByGameId(gameId)).willReturn(List.of(activePlayer, jailedPlayer));
			given(playerPosService.findByGamePlayerId("active-player")).willReturn(activePos);
			given(playerPosService.findByGamePlayerId("jailed-player")).willReturn(jailedPos);

			// when
			gameSchedulerService.broadcastGameStatus();

			// then
			@SuppressWarnings("unchecked")
			ArgumentCaptor<List<GamePlayerRedis>> jailCaptor = ArgumentCaptor.forClass(List.class);
			verify(messagingTemplate).convertAndSend(eq("/send/game/" + gameId + "/jail"), jailCaptor.capture());

			List<GamePlayerRedis> jailedPlayers = jailCaptor.getValue();
			assertThat(jailedPlayers).hasSize(1);
			assertThat(jailedPlayers.get(0).getStatus()).isEqualTo(PlayerStatus.JAILED);
		}

		@Test
		@DisplayName("수감된 플레이어가 없으면 빈 리스트를 브로드캐스트한다")
		void broadcastEmptyJailList() {
			// given
			String gameId = "game-123";

			GameRedis activeGame = GameRedis.builder()
					.id(gameId)
					.startedAt(LocalDateTime.now().minusMinutes(5))
					.endedAt(null)
					.build();

			GamePlayerRedis activePlayer = GamePlayerRedis.builder()
					.id("active-player")
					.gameId(gameId)
					.team(Team.THIEF)
					.status(PlayerStatus.ACTIVE)
					.build();

			GamePlayerPosRedis activePos = GamePlayerPosRedis.builder()
					.gamePlayerId("active-player")
					.lat(BigDecimal.ZERO)
					.lng(BigDecimal.ZERO)
					.build();

			given(gameService.findAll()).willReturn(List.of(activeGame));
			given(playerService.findByGameId(gameId)).willReturn(List.of(activePlayer));
			given(playerPosService.findByGamePlayerId("active-player")).willReturn(activePos);

			// when
			gameSchedulerService.broadcastGameStatus();

			// then
			@SuppressWarnings("unchecked")
			ArgumentCaptor<List<GamePlayerRedis>> jailCaptor = ArgumentCaptor.forClass(List.class);
			verify(messagingTemplate).convertAndSend(eq("/send/game/" + gameId + "/jail"), jailCaptor.capture());

			List<GamePlayerRedis> jailedPlayers = jailCaptor.getValue();
			assertThat(jailedPlayers).isEmpty();
		}
	}
}
