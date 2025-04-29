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

    /**
     * Handles PublicShareException and returns a ResponseEntity with an error response.
     *
     * @param ex      the PublicShareException to handle
     * @param request the HttpServletRequest object
     *
     * @return a ResponseEntity with an error response
     */
    @ExceptionHandler(PublicShareException.class)
    public ResponseEntity<ErrorResponse> handlePublicShareException(
        PublicShareException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.PUBLIC_SHARE_ERROR,
            request,
            ex.getMessage());
    }

    /**
     * Handles ItemPasswordVerificationFailedException and returns a ResponseEntity with an error
     * response.
     *
     * @param ex      the ItemPasswordVerificationFailedException to handle
     * @param request the HttpServletRequest object
     *
     * @return a ResponseEntity with an error response
     */
    @ExceptionHandler(UnauthenticatedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthenticatedException(
        UnauthenticatedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ErrorCode.HEADER_MISSING, request,
            ex.getMessage());
    }

    /**
     * Handles ItemPasswordVerificationFailedException and returns a ResponseEntity with an error
     * response.
     *
     * @param ex      the ItemPasswordVerificationFailedException to handle
     * @param request the HttpServletRequest object
     *
     * @return a ResponseEntity with an error response
     */
    @ExceptionHandler(NotEnoughPermissionException.class)
    public ResponseEntity<ErrorResponse> handleNotEnoughPermissionException(
        NotEnoughPermissionException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, ErrorCode.PERMISSION_DENIED, request,
            ex.getMessage());
    }

    /**
     * Handles ItemPasswordVerificationFailedException and returns a ResponseEntity with an error
     * response.
     *
     * @param ex      the ItemPasswordVerificationFailedException to handle
     * @param request the HttpServletRequest object
     *
     * @return a ResponseEntity with an error response
     */
    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleItemNotFoundException(
        ItemNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ErrorCode.ITEM_NOT_FOUND, request,
            ex.getMessage());
    }

    /**
     * Handles ItemPasswordVerificationFailedException and returns a ResponseEntity with an error
     * response.
     *
     * @param ex      the ItemPasswordVerificationFailedException to handle
     * @param request the HttpServletRequest object
     *
     * @return a ResponseEntity with an error response
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(
        UserNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND, request,
            ex.getMessage());
    }

    /**
     * Handles ItemPasswordVerificationFailedException and returns a ResponseEntity with an error
     * response.
     *
     * @param ex      the ItemPasswordVerificationFailedException to handle
     * @param request the HttpServletRequest object
     *
     * @return a ResponseEntity with an error response
     */
    @ExceptionHandler(ItemViewException.class)
    public ResponseEntity<ErrorResponse> handleItemViewException(
        ItemViewException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ErrorCode.ITEM_VIEW_ERROR, request,
            ex.getMessage());
    }

    /**
     * Handles ItemPasswordVerificationFailedException and returns a ResponseEntity with an error
     * response.
     *
     * @param ex      the ItemPasswordVerificationFailedException to handle
     * @param request the HttpServletRequest object
     *
     * @return a ResponseEntity with an error response
     */
    @ExceptionHandler(ItemProtectedWithPasswordException.class)
    public ResponseEntity<ErrorResponse> handleItemProtectedWithPasswordException(
        ItemProtectedWithPasswordException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, ErrorCode.ITEM_PROTECTED_WITH_PASSWORD,
            request,
            ex.getMessage());
    }

    /**
     * Handles ItemPasswordVerificationFailedException and returns a ResponseEntity with an error
     * response.
     *
     * @param ex      the ItemPasswordVerificationFailedException to handle
     * @param request the HttpServletRequest object
     *
     * @return a ResponseEntity with an error response
     */
    @ExceptionHandler(ItemPasswordVerificationFailedException.class)
    public ResponseEntity<ErrorResponse> handleItemPasswordVerificationFailedException(
        ItemPasswordVerificationFailedException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.FORBIDDEN, ErrorCode.ITEM_WRONG_PASSWORD,
            request,
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
