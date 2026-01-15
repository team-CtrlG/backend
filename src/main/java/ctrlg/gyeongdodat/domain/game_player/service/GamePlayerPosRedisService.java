package ctrlg.gyeongdodat.domain.game_player.service;

import ctrlg.gyeongdodat.domain.game_player.entity.GamePlayerPosRedis;
import ctrlg.gyeongdodat.domain.game_player.repository.GamePlayerPosRedisRepository;
import ctrlg.gyeongdodat.domain.game_player.service.command.GamePlayerPosCreateCommand;
import ctrlg.gyeongdodat.domain.game_player.service.command.GamePlayerPosUpdateCommand;
import ctrlg.gyeongdodat.global.exception.ErrorCode;
import ctrlg.gyeongdodat.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class GamePlayerPosRedisService {

    private final GamePlayerPosRedisRepository gamePlayerPosRedisRepository;

    public GamePlayerPosRedis create(GamePlayerPosCreateCommand command) {
        GamePlayerPosRedis pos = command.toEntity();
        return gamePlayerPosRedisRepository.save(pos);
    }

    public GamePlayerPosRedis findByGamePlayerId(String gamePlayerId) {
        return gamePlayerPosRedisRepository.findById(gamePlayerId)
                .orElseThrow(() -> new GlobalException(ErrorCode.GAME_PLAYER_POS_NOT_FOUND));
    }

    public GamePlayerPosRedis findByGamePlayerIdOrNull(String gamePlayerId) {
        return gamePlayerPosRedisRepository.findById(gamePlayerId).orElse(null);
    }

    public List<GamePlayerPosRedis> findAll() {
        return StreamSupport.stream(gamePlayerPosRedisRepository.findAll().spliterator(), false)
                .toList();
    }

    public GamePlayerPosRedis update(String gamePlayerId, GamePlayerPosUpdateCommand command) {
        GamePlayerPosRedis pos = findByGamePlayerId(gamePlayerId);
        pos.update(command);
        return gamePlayerPosRedisRepository.save(pos);
    }

    public void delete(String gamePlayerId) {
        if (!gamePlayerPosRedisRepository.existsById(gamePlayerId)) {
            throw new GlobalException(ErrorCode.GAME_PLAYER_POS_NOT_FOUND);
        }
        gamePlayerPosRedisRepository.deleteById(gamePlayerId);
    }
}
