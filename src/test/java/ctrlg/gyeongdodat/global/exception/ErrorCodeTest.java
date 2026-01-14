package ctrlg.gyeongdodat.global.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorCodeTest {

    @ParameterizedTest
    @EnumSource(ErrorCode.class)
    @DisplayName("모든 ErrorCode는 HttpStatus를 가진다")
    void allErrorCodesHaveHttpStatus(ErrorCode errorCode) {
        // then
        assertThat(errorCode.getHttpStatus()).isNotNull();
    }

    @ParameterizedTest
    @EnumSource(ErrorCode.class)
    @DisplayName("모든 ErrorCode는 메시지를 가진다")
    void allErrorCodesHaveMessage(ErrorCode errorCode) {
        // then
        assertThat(errorCode.getMessage()).isNotBlank();
    }

    @Test
    @DisplayName("GAME_NOT_FOUND는 NOT_FOUND 상태를 가진다")
    void gameNotFoundHasCorrectStatus() {
        // then
        assertThat(ErrorCode.GAME_NOT_FOUND.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("GAME_PLAYER_NOT_FOUND는 NOT_FOUND 상태를 가진다")
    void gamePlayerNotFoundHasCorrectStatus() {
        // then
        assertThat(ErrorCode.GAME_PLAYER_NOT_FOUND.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("GAME_PLAYER_POS_NOT_FOUND는 NOT_FOUND 상태를 가진다")
    void gamePlayerPosNotFoundHasCorrectStatus() {
        // then
        assertThat(ErrorCode.GAME_PLAYER_POS_NOT_FOUND.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("GAME_LOG_NOT_FOUND는 NOT_FOUND 상태를 가진다")
    void gameLogNotFoundHasCorrectStatus() {
        // then
        assertThat(ErrorCode.GAME_LOG_NOT_FOUND.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("GAME_PLAYER_POS_HISTORY_NOT_FOUND는 NOT_FOUND 상태를 가진다")
    void gamePlayerPosHistoryNotFoundHasCorrectStatus() {
        // then
        assertThat(ErrorCode.GAME_PLAYER_POS_HISTORY_NOT_FOUND.getHttpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
