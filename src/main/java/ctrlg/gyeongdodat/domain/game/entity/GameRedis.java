package ctrlg.gyeongdodat.domain.game.entity;

import ctrlg.gyeongdodat.domain.game.dto.Location;
import ctrlg.gyeongdodat.domain.game.enums.WinTeam;
import ctrlg.gyeongdodat.domain.game.service.command.GameUpdateCommand;
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
import java.util.List;

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
    private List<Location> gameArea = new java.util.ArrayList<>();

    @Builder.Default
    private WinTeam winTeam = WinTeam.UNKNOWN;

    private String rulesJson;

    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    public void update(GameUpdateCommand command) {
        if (command.getWinTeam() != null) {
            this.winTeam = command.getWinTeam();
        }
        if (command.getStartedAt() != null) {
            this.startedAt = command.getStartedAt();
        }
        if (command.getEndedAt() != null) {
            this.endedAt = command.getEndedAt();
        }
        if (command.getGameArea() != null) {
            this.gameArea = command.getGameArea();
        }
        this.updatedAt = LocalDateTime.now();
    }
}
