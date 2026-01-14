package ctrlg.gyeongdodat.domain.game_player.service;

import com.github.f4b6a3.ulid.UlidCreator;
import ctrlg.gyeongdodat.domain.game_player.entity.GamePlayerRedis;
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
}
