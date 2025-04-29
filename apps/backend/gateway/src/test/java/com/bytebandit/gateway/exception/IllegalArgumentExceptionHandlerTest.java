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
class IllegalArgumentExceptionHandlerTest {

    private final GlobalExceptionHandler advice = new GlobalExceptionHandler();

    /**
     * Verify that handleIllegalArgument returns an ErrorResponse with.
     */
    @Test
    void handleIllegalArgument_shouldReturnBadRequestErrorResponse() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/test");

        String message = "Invalid input format.";
        IllegalArgumentException ex = new IllegalArgumentException(message);

        ErrorResponse error = advice.handleIllegalArgument(ex, request);

        assertThat(error.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(error.getError()).isEqualTo(HttpStatus.BAD_REQUEST.getReasonPhrase());
        assertThat(error.getMessage()).isEqualTo(ErrorCode.INVALID_INPUT_FORMAT.getMessage());
        assertThat(error.getErrorCode()).isEqualTo(ErrorCode.INVALID_INPUT_FORMAT.getCode());
        assertThat(error.getDetails()).isEqualTo(message);
        assertThat(error.getPath()).isEqualTo("/api/test");
        assertThat(error.getTimestamp()).isNotNull();
    }
}
