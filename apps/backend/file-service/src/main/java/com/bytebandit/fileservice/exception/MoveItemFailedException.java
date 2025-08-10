package com.bytebandit.fileservice.exception;

public class MoveItemFailedException extends RuntimeException {
    public MoveItemFailedException(String message) {
        super(message);
    }
}
