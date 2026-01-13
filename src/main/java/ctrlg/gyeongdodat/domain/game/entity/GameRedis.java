package ctrlg.gyeongdodat.domain.game.entity;

import ctrlg.gyeongdodat.domain.game.enums.WinTeam;
import ctrlg.gyeongdodat.global.entity.BaseRedisTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
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
@RedisHash(value = "game")
public class GameRedis extends BaseRedisTimeEntity {

    @Id
    private String id;

    private Integer gameTimeSec;

    private Integer hideTimeSec;

    private BigDecimal jailLat;

    private BigDecimal jailLng;

    private String jailImage;

    @Builder.Default
    private WinTeam winTeam = WinTeam.UNKNOWN;

    private String rulesJson;

    private LocalDateTime startedAt;

    private LocalDateTime endedAt;
}
