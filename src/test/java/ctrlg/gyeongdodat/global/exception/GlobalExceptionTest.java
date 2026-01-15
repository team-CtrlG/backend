package ctrlg.gyeongdodat.global.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionTest {

    @Test
    @DisplayName("ErrorCode로 GlobalException 생성 시 ErrorCode와 메시지가 정상 반환된다")
    void createWithErrorCode() {
        // given
        ErrorCode errorCode = ErrorCode.GAME_NOT_FOUND;

        // when
        GlobalException exception = new GlobalException(errorCode);

        // then
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getMessage()).isEqualTo(errorCode.getMessage());
    }

    @Test
    @DisplayName("ErrorCode와 커스텀 메시지로 GlobalException 생성 시 커스텀 메시지가 반환된다")
    void createWithErrorCodeAndCustomMessage() {
        // given
        ErrorCode errorCode = ErrorCode.GAME_NOT_FOUND;
        String customMessage = "게임 ID: 123을 찾을 수 없습니다.";

        // when
        GlobalException exception = new GlobalException(errorCode, customMessage);

        // then
        assertThat(exception.getErrorCode()).isEqualTo(errorCode);
        assertThat(exception.getMessage()).isEqualTo(customMessage);
    }

    @Test
    @DisplayName("GlobalException은 RuntimeException을 상속한다")
    void extendsRuntimeException() {
        // given
        GlobalException exception = new GlobalException(ErrorCode.GAME_NOT_FOUND);

        // then
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
