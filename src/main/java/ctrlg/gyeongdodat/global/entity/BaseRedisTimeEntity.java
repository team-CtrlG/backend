package ctrlg.gyeongdodat.global.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseRedisTimeEntity {

    @Builder.Default
    protected LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    protected LocalDateTime updatedAt = LocalDateTime.now();
}
