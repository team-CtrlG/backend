package ctrlg.gyeongdodat.domain.game_player.repository;

import ctrlg.gyeongdodat.domain.game_player.entity.GamePlayerRedis;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GamePlayerRedisRepository extends CrudRepository<GamePlayerRedis, String> {

    List<GamePlayerRedis> findByGameId(String gameId);
}
