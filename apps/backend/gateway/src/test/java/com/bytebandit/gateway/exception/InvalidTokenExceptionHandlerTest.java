package com.bytebandit.gateway.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import lib.core.dto.response.ErrorResponse;
import lib.core.enums.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.http.HttpStatus;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InvalidTokenExceptionHandlerTest {

    private final GlobalExceptionHandler advice = new GlobalExceptionHandler();

    /**
     * Verify that handleInvalidToken returns an ErrorResponse with.
     *  - status 401
     *  - errorCode & code from ErrorCode.TOKEN_INVALID
     *  - details matching exception message
     *  - path from HttpServletRequest
     */
    @Test
    void handleInvalidToken_shouldReturnUnauthorizedErrorResponse() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/secure/data");

        String msg = "Invalid authentication token.";
        InvalidTokenException ex = new InvalidTokenException(msg);

        ErrorResponse error = advice.handleInvalidToken(ex, request);

        assertThat(error.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(error.getError()).isEqualTo(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        assertThat(error.getMessage()).isEqualTo(ErrorCode.TOKEN_INVALID.getMessage());
        assertThat(error.getErrorCode()).isEqualTo(ErrorCode.TOKEN_INVALID.getCode());
        assertThat(error.getDetails()).isEqualTo(msg);
        assertThat(error.getPath()).isEqualTo("/api/secure/data");
        assertThat(error.getTimestamp()).isNotNull();
    }
}

