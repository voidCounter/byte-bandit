package lib.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    AUTH_INVALID_CREDENTIALS("AUTH-01", "Invalid credentials provided."),
    TOKEN_INVALID("SEC-02", "Invalid authentication token."),

    USER_NOT_FOUND("USER-01", "User not found."),

    INVALID_INPUT_FORMAT("VALID-01", "Invalid input format."),
    REQUEST_VALIDATION_FAILED("VALID-02", "Request validation failed."),

    DB_CONSTRAINT_VIOLATION("DB-01", "Database constraint violation."),

    INTERNAL_SERVER_ERROR("SYS-01", "An internal server error occurred.");

    private final String code;
    private final String message;
}

