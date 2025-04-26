package lib.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    
    AUTH_INVALID_CREDENTIALS("AUTH-01", "Invalid email or password provided."),
    AUTH_TOKEN_EXPIRED("AUTH-02", "Authentication token has expired."),
    AUTH_ACCESS_DENIED("AUTH-03", "Access denied for the requested resource."),

    HEADER_MISSING("HEADER-01", "Required header is missing."),
    
    DATABASE_ERROR("DB-02", "Database error occurred."),
    
    USER_NOT_FOUND("USER-01", "User not found."),
    USER_ALREADY_EXISTS("USER-02", "User already exists."),
    USER_INVALID_INPUT("USER-03", "Invalid user input provided."),
    EMAIL_ALREADY_USED("USER-04", "Email is already in use."),
    EMAIL_ALREADY_VERIFIED("USER-05", "Email is already verified."),
    
    IMAGE_UPLOAD_FAILED("IMG-01", "Image upload failed."),
    INVALID_IMAGE_FORMAT("IMG-02", "Invalid image format."),
    
    
    INVALID_EMAIL("SEC-01", "Invalid email address provided."),
    TOKEN_INVALID("SEC-02", "Invalid authentication token."),
    PASSWORD_TOO_SHORT("SEC-04", "Password must be at least 8 characters."),
    PASSWORD_TOO_WEAK("SEC-05", "Password must contain a digit, uppercase, "
        + "lowercase, and a special character."),
    PASSWORD_NULL("SEC-06", "Password field cannot be null."),
    INVALID_PASSWORD("SEC-07", "Invalid password format."),
    
    EMAIL_SEND_FAILED("EMAIL-01", "Failed to send email."),
    
    TOO_MANY_REQUESTS("API-01", "Too many requests. Please try again later."),
    
    USER_NOT_AUTHENTICATED("AUTH-04", "User not authenticated."),
    
    INVALID_INPUT_FORMAT("VALID-01", "Invalid input format."),
    REQUEST_VALIDATION_FAILED("VALID-02", "Request validation failed."),
    
    INTERNAL_SERVER_ERROR("SYS-01", "An internal server error occurred."),
    SERVICE_UNAVAILABLE("SYS-02", "The service is temporarily unavailable."),
    DB_CONSTRAINT_VIOLATION("DB-01", "Database constraint violation."),
    
    UNKNOWN_ERROR("UNKNOWN", "An unknown error has occurred.");
    
    private final String code;
    private final String message;
}

