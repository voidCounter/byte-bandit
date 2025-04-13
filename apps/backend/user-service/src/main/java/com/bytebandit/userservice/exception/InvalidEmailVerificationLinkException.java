package com.bytebandit.userservice.exception;

public class InvalidEmailVerificationLinkException extends RuntimeException {
    public InvalidEmailVerificationLinkException(String message) {
        super(message);
    }

    public InvalidEmailVerificationLinkException(String message, Throwable cause) {
        super(message, cause);
    }
}