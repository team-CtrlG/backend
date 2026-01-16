package ctrlg.gyeongdodat.domain.game_player.service;

import com.github.f4b6a3.ulid.UlidCreator;
import ctrlg.gyeongdodat.domain.game_player.entity.GamePlayerRedis;
import ctrlg.gyeongdodat.domain.game_player.enums.Team;
import ctrlg.gyeongdodat.domain.game_player.repository.GamePlayerRedisRepository;
import ctrlg.gyeongdodat.domain.game_player.service.command.GamePlayerCreateCommand;
import ctrlg.gyeongdodat.domain.game_player.service.command.GamePlayerUpdateCommand;
import ctrlg.gyeongdodat.global.exception.ErrorCode;
import ctrlg.gyeongdodat.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class GamePlayerRedisService {

    private final GamePlayerRedisRepository gamePlayerRedisRepository;

    public GamePlayerRedis create(GamePlayerCreateCommand command) {
        String id = UlidCreator.getUlid().toString();
        GamePlayerRedis gamePlayer = command.toEntity(id);
        return gamePlayerRedisRepository.save(gamePlayer);
    }

    public GamePlayerRedis findById(String id) {
        return gamePlayerRedisRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCode.GAME_PLAYER_NOT_FOUND));
    }

    public GamePlayerRedis findByIdOrNull(String id) {
        return gamePlayerRedisRepository.findById(id).orElse(null);
    }

    public List<GamePlayerRedis> findByGameId(String gameId) {
        return gamePlayerRedisRepository.findByGameId(gameId);
    }

    public List<GamePlayerRedis> findAll() {
        return StreamSupport.stream(gamePlayerRedisRepository.findAll().spliterator(), false)
                .toList();
    }

    public GamePlayerRedis update(String id, GamePlayerUpdateCommand command) {
        GamePlayerRedis gamePlayer = findById(id);
        gamePlayer.update(command);
        return gamePlayerRedisRepository.save(gamePlayer);
    }

    public void delete(String id) {
        if (!gamePlayerRedisRepository.existsById(id)) {
            throw new GlobalException(ErrorCode.GAME_PLAYER_NOT_FOUND);
        }
        gamePlayerRedisRepository.deleteById(id);
    }

    public void deleteByGameId(String gameId) {
        List<GamePlayerRedis> players = findByGameId(gameId);
        gamePlayerRedisRepository.deleteAll(players);
    }

    public GamePlayerRedis findByGameIdAndThiefNumber(String gameId, Integer thiefNumber) {
        return gamePlayerRedisRepository.findByGameIdAndThiefNumber(gameId, thiefNumber);
    }

    /**
     * 해당 게임의 다음 도둑 번호를 반환한다
     * @param gameId 게임 ID
     * @return 다음 도둑 번호 (기존 최대값 + 1, 없으면 1)
     */
    public Integer getNextThiefNumber(String gameId) {
        List<GamePlayerRedis> players = findByGameId(gameId);
        return players.stream()
                .filter(p -> p.getTeam() == Team.THIEF)
                .filter(p -> p.getThiefNumber() != null)
                .map(GamePlayerRedis::getThiefNumber)
                .max(Integer::compareTo)
                .orElse(0) + 1;
    }

    /**
     * 도둑인 경우 번호를 자동 부여한다
     * @param playerId 플레이어 ID
     * @return 업데이트된 플레이어 (경찰이면 번호 없이 반환)
     */
    public GamePlayerRedis assignThiefNumber(String playerId) {
        GamePlayerRedis player = findById(playerId);

        // 경찰이면 번호 부여하지 않음
        if (player.getTeam() != Team.THIEF) {
            return player;
        }

        // 이미 번호가 있으면 기존 번호 유지
        if (player.getThiefNumber() != null) {
            return player;
        }

        // 다음 번호 부여
        Integer nextNumber = getNextThiefNumber(player.getGameId());
        GamePlayerUpdateCommand updateCommand = GamePlayerUpdateCommand.builder()
                .thiefNumber(nextNumber)
                .build();
        return update(playerId, updateCommand);
    }
}
