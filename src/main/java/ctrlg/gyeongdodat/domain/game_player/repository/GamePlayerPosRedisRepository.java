package ctrlg.gyeongdodat.domain.game_player.repository;

import ctrlg.gyeongdodat.domain.game_player.entity.GamePlayerPosRedis;
import org.springframework.data.repository.CrudRepository;

public interface GamePlayerPosRedisRepository extends CrudRepository<GamePlayerPosRedis, String> {
}
