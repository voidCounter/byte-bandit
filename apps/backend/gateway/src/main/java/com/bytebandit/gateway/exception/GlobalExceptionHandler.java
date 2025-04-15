package com.bytebandit.gateway.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lib.core.dto.response.ErrorResponse;
import lib.core.enums.ErrorCode;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Supplier<UUID> uuidSupplier = UUID::randomUUID;

    /**
     * Handles BadCredentialsException thrown by Spring Security.
     *
     * @param ex      the exception
     * @param request the HTTP request
     *
     * @return an ErrorResponse with status UNAUTHORIZED
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleBadCredentials(
        BadCredentialsException ex,
        HttpServletRequest request
    ) {
        return buildError(
            HttpStatus.UNAUTHORIZED,
            ErrorCode.AUTH_INVALID_CREDENTIALS,
            ex.getMessage(),
            request.getRequestURI()
        );
    }

    /**
     * Handles InvalidTokenException thrown by the application.
     *
     * @param ex      the exception
     * @param request the HTTP request
     *
     * @return an ErrorResponse with status UNAUTHORIZED
     */
    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleInvalidToken(InvalidTokenException ex, HttpServletRequest request) {
        return buildError(
            HttpStatus.UNAUTHORIZED,
            ErrorCode.TOKEN_INVALID,
            ex.getMessage(),
            request.getRequestURI()
        );
    }

    /**
     * Handles UsernameNotFoundException thrown by Spring Security.
     *
     * @param ex      the exception
     * @param request the HTTP request
     *
     * @return an ErrorResponse with status NOT_FOUND
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(
        UsernameNotFoundException ex,
        HttpServletRequest request
    ) {
        return buildError(
            HttpStatus.NOT_FOUND,
            ErrorCode.USER_NOT_FOUND,
            ex.getMessage(),
            request.getRequestURI()
        );
    }

    /**
     * Handles IllegalArgumentException thrown by the application.
     *
     * @param ex      the exception
     * @param request the HTTP request
     *
     * @return an ErrorResponse with status BAD_REQUEST
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgument(
        IllegalArgumentException ex,
        HttpServletRequest request
    ) {
        return buildError(
            HttpStatus.BAD_REQUEST,
            ErrorCode.INVALID_INPUT_FORMAT,
            ex.getMessage(),
            request.getRequestURI()
        );
    }

    /**
     * Handles MethodArgumentNotValidException thrown by Spring MVC.
     *
     * @param ex      the exception
     * @param request the HTTP request
     *
     * @return an ErrorResponse with status BAD_REQUEST
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationErrors(
        MethodArgumentNotValidException ex,
        HttpServletRequest request
    ) {
        String details = ex.getBindingResult().getAllErrors().stream()
                             .map(DefaultMessageSourceResolvable::getDefaultMessage)
                             .collect(Collectors.joining("; "));

        return buildError(
            HttpStatus.BAD_REQUEST,
            ErrorCode.REQUEST_VALIDATION_FAILED,
            "Validation failed",
            request.getRequestURI(),
            details
        );
    }

    /**
     * Handles DataIntegrityViolationException thrown by Spring Data JPA.
     *
     * @param ex      the exception
     * @param request the HTTP request
     *
     * @return an ErrorResponse with status CONFLICT
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDbViolation(
        DataIntegrityViolationException ex,
        HttpServletRequest request
    ) {
        return buildError(
            HttpStatus.CONFLICT,
            ErrorCode.DB_CONSTRAINT_VIOLATION,
            "Database constraint violation",
            request.getRequestURI(),
            ex.getMessage()
        );
    }

    /**
     * Handles all uncaught exceptions.
     *
     * @param ex      the exception
     * @param request the HTTP request
     *
     * @return an ErrorResponse with status INTERNAL_SERVER_ERROR
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAllUncaughtExceptions(Exception ex, HttpServletRequest request) {
        return buildError(
            HttpStatus.INTERNAL_SERVER_ERROR,
            ErrorCode.INTERNAL_SERVER_ERROR,
            ex.getMessage(),
            request.getRequestURI()
        );
    }

    /**
     * Handles UserNotAuthenticatedException thrown by the application.
     *
     * @param ex      the exception
     * @param request the HTTP request
     *
     * @return an ErrorResponse with status UNAUTHORIZED
     */
    @ExceptionHandler(UserNotAuthenticatedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleUserNotAuthenticated(
        UserNotAuthenticatedException ex,
        HttpServletRequest request
    ) {
        return buildError(
            HttpStatus.UNAUTHORIZED,
            ErrorCode.USER_NOT_AUTHENTICATED,
            ex.getMessage(),
            request.getRequestURI()
        );
    }

    private ErrorResponse buildError(
        HttpStatus status,
        ErrorCode code,
        String message,
        String path
    ) {
        return ErrorResponse.create(
            status,
            status.getReasonPhrase(),
            message,
            code.getCode(),
            code.getMessage(),
            path,
            uuidSupplier
        );
    }

    private ErrorResponse buildError(
        HttpStatus status,
        ErrorCode code,
        String message,
        String path,
        String details
    ) {
        return ErrorResponse.create(
            status,
            status.getReasonPhrase(),
            message,
            code.getCode(),
            details,
            path,
            uuidSupplier
        );
    }
}

