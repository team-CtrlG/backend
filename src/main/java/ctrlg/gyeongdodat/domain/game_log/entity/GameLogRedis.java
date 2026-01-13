package ctrlg.gyeongdodat.domain.game_log.entity;

import ctrlg.gyeongdodat.domain.game_log.enums.GameAction;
import ctrlg.gyeongdodat.global.entity.BaseRedisTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "game_log")
public class GameLogRedis extends BaseRedisTimeEntity {

    @Id
    private Long id;

    @Indexed
    private String gameId;

    private String actorPlayerId;

    private String targetPlayerId;

    private GameAction action;
}
