package com.bytebandit.userservice.enums;


import lombok.Getter;

@Getter
public enum ErrorCode {
    
    // Authentication & Authorization Errors
    AUTH_INVALID_CREDENTIALS("AUTH-01", "Invalid credentials provided."),
    AUTH_TOKEN_EXPIRED("AUTH-02", "Authentication token has expired."),
    AUTH_ACCESS_DENIED("AUTH-03", "Access denied for the requested resource."),
    
    // User Errors
    USER_NOT_FOUND("USER-01", "User not found."),
    USER_ALREADY_EXISTS("USER-02", "User already exists."),
    USER_INVALID_INPUT("USER-03", "Invalid user input provided."),
    EMAIL_ALREADY_USED("USER-04", "Email is already in use."),
    
    // Image Upload Errors
    IMAGE_UPLOAD_FAILED("IMG-01", "Image upload failed."),
    INVALID_IMAGE_FORMAT("IMG-02", "Invalid image format."),
    
    // Password & Security Errors
    INVALID_PASSWORD("SEC-01", "Invalid password provided."),
    TOKEN_INVALID("SEC-02", "Invalid authentication token."),
    REFRESH_TOKEN_EXPIRED("SEC-03", "Refresh token has expired."),
    
    // API Rate Limit Errors
    TOO_MANY_REQUESTS("API-01", "Too many requests. Please try again later."),
    
    // General Validation Errors
    INVALID_INPUT_FORMAT("VALID-01", "Invalid input format."),
    REQUEST_VALIDATION_FAILED("VALID-02", "Request validation failed."),
    
    // System Errors
    INTERNAL_SERVER_ERROR("SYS-01", "An internal server error occurred."),
    SERVICE_UNAVAILABLE("SYS-02", "The service is temporarily unavailable."),
    // Database Errors
    DB_CONSTRAINT_VIOLATION("DB-01", "Database constraint violation."),
    
    // Unknown Error
    UNKNOWN_ERROR("UNKNOWN", "An unknown error has occurred.");
    
    private final String code;
    private final String message;
    
    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    @Override
    public String toString() {
        return code + ": " + message;
    }
}
