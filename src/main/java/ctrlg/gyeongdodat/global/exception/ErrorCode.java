package ctrlg.gyeongdodat.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Game
    GAME_NOT_FOUND(HttpStatus.NOT_FOUND, "게임을 찾을 수 없습니다."),
    INVALID_ATTENDANCE_CODE(HttpStatus.BAD_REQUEST, "출석 코드가 올바르지 않습니다."),
    ALREADY_ATTENDED(HttpStatus.BAD_REQUEST, "이미 출석 처리되었습니다."),

    // GamePlayer
    GAME_PLAYER_NOT_FOUND(HttpStatus.NOT_FOUND, "게임 플레이어를 찾을 수 없습니다."),
    NOT_POLICE(HttpStatus.FORBIDDEN, "경찰만 체포할 수 있습니다."),
    NOT_THIEF(HttpStatus.FORBIDDEN, "도둑만 구출할 수 있습니다."),
    THIEF_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 도둑 번호의 플레이어를 찾을 수 없습니다."),
    ALREADY_JAILED(HttpStatus.BAD_REQUEST, "이미 수감된 도둑입니다."),
    NOT_JAILED(HttpStatus.BAD_REQUEST, "수감 상태가 아닌 플레이어입니다."),
    RESCUER_IS_JAILED(HttpStatus.FORBIDDEN, "수감 상태에서는 구출할 수 없습니다."),

    // GamePlayerPos
    GAME_PLAYER_POS_NOT_FOUND(HttpStatus.NOT_FOUND, "게임 플레이어 위치를 찾을 수 없습니다."),

    // GameLog
    GAME_LOG_NOT_FOUND(HttpStatus.NOT_FOUND, "게임 로그를 찾을 수 없습니다."),

    // GamePlayerPosHistory
    GAME_PLAYER_POS_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND, "게임 플레이어 위치 이력을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
