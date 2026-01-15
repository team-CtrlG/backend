package ctrlg.gyeongdodat.domain.game_log.service;

import com.github.f4b6a3.ulid.UlidCreator;
import ctrlg.gyeongdodat.domain.game_log.entity.GameLogRedis;
import ctrlg.gyeongdodat.domain.game_log.repository.GameLogRedisRepository;
import ctrlg.gyeongdodat.domain.game_log.service.command.GameLogCreateCommand;
import ctrlg.gyeongdodat.global.exception.ErrorCode;
import ctrlg.gyeongdodat.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class GameLogRedisService {

    private final GameLogRedisRepository gameLogRedisRepository;

    public GameLogRedis create(GameLogCreateCommand command) {
        String id = UlidCreator.getUlid().toString();
        GameLogRedis gameLog = command.toEntity(id);
        return gameLogRedisRepository.save(gameLog);
    }

    public GameLogRedis findById(String id) {
        return gameLogRedisRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCode.GAME_LOG_NOT_FOUND));
    }

    public GameLogRedis findByIdOrNull(String id) {
        return gameLogRedisRepository.findById(id).orElse(null);
    }

    public List<GameLogRedis> findByGameId(String gameId) {
        return gameLogRedisRepository.findByGameId(gameId);
    }

    public List<GameLogRedis> findAll() {
        return StreamSupport.stream(gameLogRedisRepository.findAll().spliterator(), false)
                .toList();
    }

    public void delete(String id) {
        if (!gameLogRedisRepository.existsById(id)) {
            throw new GlobalException(ErrorCode.GAME_LOG_NOT_FOUND);
        }
        gameLogRedisRepository.deleteById(id);
    }

    public void deleteByGameId(String gameId) {
        List<GameLogRedis> logs = findByGameId(gameId);
        gameLogRedisRepository.deleteAll(logs);
    }
}
