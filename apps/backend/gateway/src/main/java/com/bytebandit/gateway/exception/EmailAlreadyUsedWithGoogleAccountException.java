package com.bytebandit.gateway.exception;

public class EmailAlreadyUsedWithGoogleAccountException extends RuntimeException {
    public EmailAlreadyUsedWithGoogleAccountException() {
        super("The email is already used with a Google account.");
    }
}
