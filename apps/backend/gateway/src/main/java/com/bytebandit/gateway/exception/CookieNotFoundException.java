package com.bytebandit.gateway.exception;

/**
 * Exception thrown when a cookie with the specified name is not found in the HTTP request.
 * This exception can be used to indicate that a required cookie is missing, allowing for
 * appropriate error handling or fallback logic in the application.
 */
public class CookieNotFoundException extends RuntimeException {
    public CookieNotFoundException(String message) {
        super(message);
    }
}
