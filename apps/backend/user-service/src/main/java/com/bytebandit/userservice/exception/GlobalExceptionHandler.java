package com.bytebandit.userservice.exception;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lib.core.dto.response.ErrorResponse;
import lib.core.dto.response.FieldValidationError;
import lib.core.enums.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Value("${client.host.uri}")
    private String clientHostUri;

    /**
     * Handles FailedEmailVerificationAttemptException.
     *
     * @param ex       The exception thrown.
     * @param response The HTTP response.
     *
     * @return ResponseEntity with a redirect to the email verification page or to login page.
     */
    @ExceptionHandler(FailedEmailVerificationAttemptException.class)
    public ResponseEntity<String> handleFailedVerificationAttemptException(
        FailedEmailVerificationAttemptException ex,
        HttpServletResponse response
    ) {
        try {
            response.sendRedirect(clientHostUri + "/email-verification");
            return ResponseEntity.status(HttpStatus.FOUND).build();
        } catch (IOException ioex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                "Failed to redirect, please go to " + clientHostUri + "/login\n");
        }

    }

    /**
     * Handles EmailAlreadyVerifiedException.
     *
     * @param ex      The exception thrown.
     * @param request The HTTP request.
     *
     * @return ResponseEntity with a BAD_REQUEST status and error details.
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex,
                                                                 HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ErrorCode.USER_ALREADY_EXISTS, request,
            ex.getMessage());
    }

    /**
     * Handles EmailAlreadyVerifiedException.
     *
     * @param ex      The exception thrown.
     * @param request The HTTP request.
     *
     * @return ResponseEntity with a BAD_REQUEST status and error details.
     */
    @ExceptionHandler(EmailAlreadyVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyVerified(
        EmailAlreadyVerifiedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ErrorCode.EMAIL_ALREADY_VERIFIED, request,
            ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex,
                                                            HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND, request,
            ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex,
                                                              HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ErrorCode.AUTH_INVALID_CREDENTIALS, request,
            ex.getMessage());
    }

    /**
     * Handles InvalidTokenException.
     *
     * @param ex      The exception thrown.
     * @param request The HTTP request.
     *
     * @return ResponseEntity with a BAD_REQUEST status and error details.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
        MethodArgumentNotValidException ex, HttpServletRequest request) {

        FieldValidationError errors = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> {
                String field = error.getField();
                String message = error.getDefaultMessage();

                ErrorCode errorCode;

                switch (field) {
                    case "email" -> errorCode = ErrorCode.INVALID_EMAIL;

                    case "password" -> {
                        if (message.contains("cannot be null")) {
                            errorCode = ErrorCode.PASSWORD_NULL;
                        } else if (message.contains("at least 8 characters")) {
                            errorCode = ErrorCode.PASSWORD_TOO_SHORT;
                        } else if (message.contains("uppercase") || message.contains("digit") || message.contains("special character")) {
                            errorCode = ErrorCode.PASSWORD_TOO_WEAK;
                        } else {
                            errorCode = ErrorCode.INVALID_PASSWORD;
                        }
                    }

                    default -> errorCode = ErrorCode.REQUEST_VALIDATION_FAILED;
                }

                return new FieldValidationError(field, errorCode, message);
            })
            .toList()
            .get(0);

        return buildResponse(HttpStatus.BAD_REQUEST, errors.getCode(), request,
            errors.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
        DataIntegrityViolationException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ErrorCode.DB_CONSTRAINT_VIOLATION, request,
            ex.getMostSpecificCause().getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
        MissingServletRequestParameterException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ErrorCode.INVALID_INPUT_FORMAT, request,
            "Missing parameter: " + ex.getParameterName());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaType(
        HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ErrorCode.INVALID_IMAGE_FORMAT,
            request, "Provided media type: " + ex.getContentType());
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ErrorResponse> handleTooManyRequests(TooManyRequestsException ex,
                                                               HttpServletRequest request) {
        return buildResponse(HttpStatus.TOO_MANY_REQUESTS, ErrorCode.TOO_MANY_REQUESTS, request,
            ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex,
                                                               HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.UNKNOWN_ERROR, request,
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