package com.bytebandit.userservice.exception;

public class FailedEmailVerificationAttemptException extends RuntimeException {
    public FailedEmailVerificationAttemptException(String message) {
        super(message);
    }
}
