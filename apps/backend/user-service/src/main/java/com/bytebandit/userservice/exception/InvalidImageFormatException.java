package com.bytebandit.userservice.exception;

import java.io.Serial;

/**
 * Exception thrown when image format validation fails. This exception is handled by
 * GlobalExceptionHandler.
 */
public class InvalidImageFormatException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidImageFormatException(String message) {
        super(message);
    }

    public InvalidImageFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidImageFormatException(Throwable cause) {
        super(cause);
    }
}