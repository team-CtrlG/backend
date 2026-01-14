package ctrlg.gyeongdodat.domain.game_log.service;

import ctrlg.gyeongdodat.domain.game_log.entity.GameLogRedis;
import ctrlg.gyeongdodat.domain.game_log.repository.GameLogRedisRepository;
import ctrlg.gyeongdodat.domain.game_log.service.command.GameLogCreateCommand;
import ctrlg.gyeongdodat.global.exception.ErrorCode;
import ctrlg.gyeongdodat.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class GameLogRedisService {

    private final GameLogRedisRepository gameLogRedisRepository;
    private final AtomicLong idGenerator = new AtomicLong(System.currentTimeMillis());

    public GameLogRedis create(GameLogCreateCommand command) {
        Long id = idGenerator.incrementAndGet();
        GameLogRedis gameLog = command.toEntity(id);
        return gameLogRedisRepository.save(gameLog);
    }

    public GameLogRedis findById(Long id) {
        return gameLogRedisRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCode.GAME_LOG_NOT_FOUND));
    }

    public GameLogRedis findByIdOrNull(Long id) {
        return gameLogRedisRepository.findById(id).orElse(null);
    }

    public List<GameLogRedis> findByGameId(String gameId) {
        return gameLogRedisRepository.findByGameId(gameId);
    }

    public List<GameLogRedis> findAll() {
        return StreamSupport.stream(gameLogRedisRepository.findAll().spliterator(), false)
                .toList();
    }

    public void delete(Long id) {
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
