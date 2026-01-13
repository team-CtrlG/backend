package ctrlg.gyeongdodat.domain.game_log.repository;

import ctrlg.gyeongdodat.domain.game_log.entity.GameLogRedis;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GameLogRedisRepository extends CrudRepository<GameLogRedis, Long> {

    List<GameLogRedis> findByGameId(String gameId);
}
