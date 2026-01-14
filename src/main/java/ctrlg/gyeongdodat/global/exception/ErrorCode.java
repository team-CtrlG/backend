package ctrlg.gyeongdodat.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Game
    GAME_NOT_FOUND(HttpStatus.NOT_FOUND, "게임을 찾을 수 없습니다."),

    // GamePlayer
    GAME_PLAYER_NOT_FOUND(HttpStatus.NOT_FOUND, "게임 플레이어를 찾을 수 없습니다."),

    // GamePlayerPos
    GAME_PLAYER_POS_NOT_FOUND(HttpStatus.NOT_FOUND, "게임 플레이어 위치를 찾을 수 없습니다."),

    // GameLog
    GAME_LOG_NOT_FOUND(HttpStatus.NOT_FOUND, "게임 로그를 찾을 수 없습니다."),

    // GamePlayerPosHistory
    GAME_PLAYER_POS_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "게임 플레이어 위치 이력을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
