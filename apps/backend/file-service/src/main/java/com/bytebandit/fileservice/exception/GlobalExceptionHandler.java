package com.bytebandit.fileservice.exception;


import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import lib.core.dto.response.ErrorResponse;
import lib.core.enums.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex,
                                                               HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.UNKNOWN_ERROR, request,
            ex.getMessage());
    }
    
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(
        DataAccessException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.DATABASE_ERROR, request,
            ex.getMessage());
    }
    
    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, ErrorCode errorCode,
                                                        HttpServletRequest request,
                                                        String details) {
        logger.error("Error occurred: status={}, errorCode={}, details={}", status, errorCode,
            details);
        return ResponseEntity.status(status).body(
            ErrorResponse.create(status, status.getReasonPhrase(), errorCode.getMessage(),
                errorCode.getCode(), details, request.getRequestURI(), UUID::randomUUID)
        );
    }
}
