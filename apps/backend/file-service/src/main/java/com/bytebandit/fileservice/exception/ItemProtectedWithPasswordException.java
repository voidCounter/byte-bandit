package com.bytebandit.fileservice.exception;

public class ItemProtectedWithPasswordException extends RuntimeException {
    public ItemProtectedWithPasswordException(String message) {
        super(message);
    }
}
