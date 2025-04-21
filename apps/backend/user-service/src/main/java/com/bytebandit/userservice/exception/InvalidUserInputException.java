package com.bytebandit.userservice.exception;

import java.io.Serial;

/**
 * Exception thrown when user input validation fails. This exception is caught by the
 * GlobalExceptionHandler to return appropriate error responses.
 */
public class InvalidUserInputException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidUserInputException(String message) {
        super(message);
    }

    public InvalidUserInputException(String message, Throwable cause) {
        super(message, cause);
    }
}
