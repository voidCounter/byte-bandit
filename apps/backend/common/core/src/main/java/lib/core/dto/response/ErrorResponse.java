package lib.core.dto.response;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;


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

    /**
     * Creates a standardized error response.
     *
     * @param status       HTTP status code.
     * @param error        HTTP error name.
     * @param message      Error message.
     * @param errorCode    Error code. Used for client-side error handling.
     * @param details      Additional error details
     * @param path         Request path. Used for debugging
     * @param uuidSupplier Supplier for generating unique error ID. It's useful for tracking errors
     *                     in logs and tracing them back to the request.
     *
     * @return ErrorResponse object
     */
    public static ErrorResponse create(HttpStatus status, String error, String message,
                                       String errorCode, String details, String path,
                                       Supplier<UUID> uuidSupplier) {
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
