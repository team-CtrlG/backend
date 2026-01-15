package ctrlg.gyeongdodat.domain.game_player.entity;

import ctrlg.gyeongdodat.domain.game_player.service.command.GamePlayerPosUpdateCommand;
import ctrlg.gyeongdodat.global.entity.BaseRedisTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "game_player_pos")
public class GamePlayerPosRedis extends BaseRedisTimeEntity {

    @Id
    private String gamePlayerId;

    private BigDecimal lat;

    private BigDecimal lng;

    public void update(GamePlayerPosUpdateCommand command) {
        if (command.getLat() != null) {
            this.lat = command.getLat();
        }
        if (command.getLng() != null) {
            this.lng = command.getLng();
        }
        this.updatedAt = LocalDateTime.now();
    }
}
