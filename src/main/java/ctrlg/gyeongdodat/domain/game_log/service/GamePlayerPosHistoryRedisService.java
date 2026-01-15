package ctrlg.gyeongdodat.domain.game_log.service;

import ctrlg.gyeongdodat.domain.game_log.entity.GamePlayerPosHistoryRedis;
import ctrlg.gyeongdodat.domain.game_log.repository.GamePlayerPosHistoryRedisRepository;
import ctrlg.gyeongdodat.domain.game_log.service.command.GamePlayerPosHistoryCreateCommand;
import ctrlg.gyeongdodat.global.exception.ErrorCode;
import ctrlg.gyeongdodat.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class GamePlayerPosHistoryRedisService {

    private final GamePlayerPosHistoryRedisRepository gamePlayerPosHistoryRedisRepository;
    private final AtomicLong idGenerator = new AtomicLong(System.currentTimeMillis());

    public GamePlayerPosHistoryRedis create(GamePlayerPosHistoryCreateCommand command) {
        Long id = idGenerator.incrementAndGet();
        GamePlayerPosHistoryRedis history = command.toEntity(id);
        return gamePlayerPosHistoryRedisRepository.save(history);
    }

    public GamePlayerPosHistoryRedis findById(Long id) {
        return gamePlayerPosHistoryRedisRepository.findById(id)
                .orElseThrow(() -> new GlobalException(ErrorCode.GAME_PLAYER_POS_HISTORY_NOT_FOUND));
    }

    public GamePlayerPosHistoryRedis findByIdOrNull(Long id) {
        return gamePlayerPosHistoryRedisRepository.findById(id).orElse(null);
    }

    public List<GamePlayerPosHistoryRedis> findByGamePlayerId(String gamePlayerId) {
        return gamePlayerPosHistoryRedisRepository.findByGamePlayerId(gamePlayerId);
    }

    public List<GamePlayerPosHistoryRedis> findAll() {
        return StreamSupport.stream(gamePlayerPosHistoryRedisRepository.findAll().spliterator(), false)
                .toList();
    }

    public void delete(Long id) {
        if (!gamePlayerPosHistoryRedisRepository.existsById(id)) {
            throw new GlobalException(ErrorCode.GAME_PLAYER_POS_HISTORY_NOT_FOUND);
        }
        gamePlayerPosHistoryRedisRepository.deleteById(id);
    }

    public void deleteByGamePlayerId(String gamePlayerId) {
        List<GamePlayerPosHistoryRedis> histories = findByGamePlayerId(gamePlayerId);
        gamePlayerPosHistoryRedisRepository.deleteAll(histories);
    }
}
