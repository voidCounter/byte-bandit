package com.bytebandit.userservice.exception;

public class ErrorSendingEmailException extends RuntimeException {
    public ErrorSendingEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
