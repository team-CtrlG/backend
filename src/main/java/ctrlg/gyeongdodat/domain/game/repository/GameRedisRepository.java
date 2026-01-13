package ctrlg.gyeongdodat.domain.game.repository;

import ctrlg.gyeongdodat.domain.game.entity.GameRedis;
import org.springframework.data.repository.CrudRepository;

public interface GameRedisRepository extends CrudRepository<GameRedis, String> {
}
