package com.bytebandit.userservice.exception;
public class InvalidImageFormatException extends RuntimeException {
    public InvalidImageFormatException(String message) {
        super(message);
    }
}