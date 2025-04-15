package com.bytebandit.gateway.exception;


/**
 * Exception thrown when operations requiring authentication are attempted by unauthenticated users.
 * This exception is typically caught by {@code GlobalExceptionHandler} and converted to a 401
 * response.
 */
public class UserNotAuthenticatedException extends RuntimeException {
    public UserNotAuthenticatedException(String message) {
        super(message);
    }
}
