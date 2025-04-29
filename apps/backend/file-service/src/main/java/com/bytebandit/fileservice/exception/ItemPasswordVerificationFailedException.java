package com.bytebandit.fileservice.exception;

public class ItemPasswordVerificationFailedException extends RuntimeException {
    public ItemPasswordVerificationFailedException(String message) {
        super(message);
    }
}
