package ctrlg.gyeongdodat.domain.game.service;

import ctrlg.gyeongdodat.api.game_player.dto.PlayerPositionResponse;
import ctrlg.gyeongdodat.domain.game.entity.GameRedis;
import ctrlg.gyeongdodat.domain.game_player.entity.GamePlayerPosRedis;
import ctrlg.gyeongdodat.domain.game_player.entity.GamePlayerRedis;
import ctrlg.gyeongdodat.domain.game_player.enums.PlayerStatus;
import ctrlg.gyeongdodat.domain.game_player.service.GamePlayerPosRedisService;
import ctrlg.gyeongdodat.domain.game_player.service.GamePlayerRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameSchedulerService {

	private final GameRedisService gameService;
	private final GamePlayerRedisService playerService;
	private final GamePlayerPosRedisService playerPosService;
	private final SimpMessagingTemplate messagingTemplate;

	/**
	 * 5초 주기로 진행 중인 게임의 플레이어 위치 및 감옥 현황을 브로드캐스트
	 */
	@Scheduled(fixedRate = 5000)
	public void broadcastGameStatus() {
		List<GameRedis> activeGames = gameService.findAll().stream()
				.filter(game -> game.getStartedAt() != null && game.getEndedAt() == null)
				.toList();

		for (GameRedis game : activeGames) {
			broadcastPlayerPositions(game.getId());
			broadcastJailStatus(game.getId());
		}
	}

	/**
	 * 플레이어 위치 정보 브로드캐스트
	 */
	private void broadcastPlayerPositions(String gameId) {
		List<GamePlayerRedis> players = playerService.findByGameId(gameId);

		List<PlayerPositionResponse> positions = players.stream()
				.map(player -> {
					GamePlayerPosRedis pos = playerPosService.findByGamePlayerId(player.getId());
					return PlayerPositionResponse.builder()
							.playerId(player.getId())
							.team(player.getTeam())
							.status(player.getStatus())
							.lat(pos.getLat())
							.lng(pos.getLng())
							.thiefNumber(player.getThiefNumber())
							.build();
				})
				.collect(Collectors.toList());

		messagingTemplate.convertAndSend("/send/game/" + gameId + "/positions", positions);
	}

	/**
	 * 감옥 현황 (수감된 플레이어 목록) 브로드캐스트
	 */
	private void broadcastJailStatus(String gameId) {
		List<GamePlayerRedis> players = playerService.findByGameId(gameId);

		List<GamePlayerRedis> jailedPlayers = players.stream()
				.filter(player -> player.getStatus() == PlayerStatus.JAILED)
				.collect(Collectors.toList());

		messagingTemplate.convertAndSend("/send/game/" + gameId + "/jail", jailedPlayers);
	}
}
