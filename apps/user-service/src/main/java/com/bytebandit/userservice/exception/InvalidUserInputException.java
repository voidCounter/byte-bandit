package com.bytebandit.userservice.exception;

/**
 * Exception thrown when user input validation fails.
 * This exception is caught by the GlobalExceptionHandler to return appropriate error responses.
 */
public class InvalidUserInputException extends RuntimeException {
    public InvalidUserInputException(String message) {
        super(message);
    }
    public InvalidUserInputException(String message, Throwable cause) {
        super(message, cause);
    }
}
