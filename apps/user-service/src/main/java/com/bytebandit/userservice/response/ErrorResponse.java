package com.bytebandit.userservice.response;

import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Supplier;

@Value
@Builder
public class ErrorResponse {
    String errorId;
    Instant timestamp;
    int status;
    String error;
    String message;
    String errorCode;
    String details;
    String path;

    public static ErrorResponse create(HttpStatus status, String error, String message, String errorCode, String details, String path, Supplier<UUID> uuidSupplier) {
        return ErrorResponse.builder()
                .errorId(uuidSupplier.get().toString())
                .timestamp(Instant.now())
                .status(status.value())
                .error(error)
                .message(message)
                .errorCode(errorCode)
                .details(details)
                .path(path)
                .build();
    }
}
