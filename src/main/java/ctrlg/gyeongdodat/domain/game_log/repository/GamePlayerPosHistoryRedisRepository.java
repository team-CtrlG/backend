package ctrlg.gyeongdodat.domain.game_log.repository;

import ctrlg.gyeongdodat.domain.game_log.entity.GamePlayerPosHistoryRedis;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GamePlayerPosHistoryRedisRepository extends CrudRepository<GamePlayerPosHistoryRedis, Long> {

    List<GamePlayerPosHistoryRedis> findByGamePlayerId(String gamePlayerId);
}
