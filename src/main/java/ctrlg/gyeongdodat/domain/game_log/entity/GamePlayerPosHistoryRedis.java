package ctrlg.gyeongdodat.domain.game_log.entity;

import ctrlg.gyeongdodat.global.entity.BaseRedisTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.math.BigDecimal;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "game_player_pos_history")
public class GamePlayerPosHistoryRedis extends BaseRedisTimeEntity {

    @Id
    private Long id;

    @Indexed
    private String gamePlayerId;

    private BigDecimal lat;

    private BigDecimal lng;
}
