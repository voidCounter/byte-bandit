package com.bytebandit.fileservice.exception;

public class NotEnoughPermissionException extends RuntimeException {
    public NotEnoughPermissionException(String message) {
        super(message);
    }
}
