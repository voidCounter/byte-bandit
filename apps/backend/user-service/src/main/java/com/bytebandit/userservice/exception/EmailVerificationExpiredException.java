package com.bytebandit.userservice.exception;

public class EmailVerificationExpiredException extends RuntimeException {
    public EmailVerificationExpiredException(String message) {
        super(message);
    }

    public EmailVerificationExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}