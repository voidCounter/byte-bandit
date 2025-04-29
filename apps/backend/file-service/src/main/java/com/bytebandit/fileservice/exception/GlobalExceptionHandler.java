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

    @ExceptionHandler({
        DataAccessException.class,
        PublicShareException.class,
        UnauthenticatedException.class,
        NotEnoughPermissionException.class,
        ItemNotFoundException.class,
        UserNotFoundException.class,
        ItemViewException.class
    })
    public ResponseEntity<ErrorResponse> handleKnownExceptions(Exception ex,
                                                               HttpServletRequest request) {
        ErrorMapping mapping = mapException(ex);
        return buildResponse(mapping.status, mapping.errorCode, request, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknownException(Exception ex, HttpServletRequest
        request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.UNKNOWN_ERROR, request,
            ex.getMessage());
    }

    private record ErrorMapping(HttpStatus status, ErrorCode errorCode) {
    }

    private ErrorMapping mapException(Exception ex) {
        if (ex instanceof DataAccessException) {
            return new ErrorMapping(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.DATABASE_ERROR);
        } else if (ex instanceof PublicShareException) {
            return new ErrorMapping(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.PUBLIC_SHARE_ERROR);
        } else if (ex instanceof UnauthenticatedException) {
            return new ErrorMapping(HttpStatus.UNAUTHORIZED, ErrorCode.HEADER_MISSING);
        } else if (ex instanceof NotEnoughPermissionException) {
            return new ErrorMapping(HttpStatus.FORBIDDEN, ErrorCode.PERMISSION_DENIED);
        } else if (ex instanceof ItemNotFoundException) {
            return new ErrorMapping(HttpStatus.NOT_FOUND, ErrorCode.ITEM_NOT_FOUND);
        } else if (ex instanceof UserNotFoundException) {
            return new ErrorMapping(HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND);
        } else if (ex instanceof ItemViewException) {
            return new ErrorMapping(HttpStatus.NOT_FOUND, ErrorCode.ITEM_VIEW_ERROR);
        } else {
            return new ErrorMapping(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.UNKNOWN_ERROR);
        }
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, ErrorCode errorCode,
                                                        HttpServletRequest request, String
                                                            details) {
        logger.error("Error occurred: status={}, errorCode={}, details={}", status, errorCode,
            details);
        return ResponseEntity.status(status).body(
            ErrorResponse.create(
                status,
                status.getReasonPhrase(),
                errorCode.getMessage(),
                errorCode.getCode(),
                details,
                request.getRequestURI(),
                UUID::randomUUID
            )
        );
    }
}
