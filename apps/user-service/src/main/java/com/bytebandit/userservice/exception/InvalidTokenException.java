package com.bytebandit.userservice.exception;
import java.io.Serial;
/**
 + * Exception thrown when a provided token is invalid, expired, or cannot be validated.
 + * Used primarily in authentication and authorization processes.
 + */
public class InvalidTokenException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
    public InvalidTokenException(Throwable cause) {
        super(cause);
    }
}

