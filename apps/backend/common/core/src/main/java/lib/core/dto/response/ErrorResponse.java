package lib.core.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import org.springframework.http.HttpStatus;


@Value
@Builder
@Getter
@Schema(
    description = "Standardized error response for API errors",
    title = "ErrorResponse",
    example = "{\"errorId\": \"123e4567-e89b-12d3-a456-426614174000\", \"timestamp\": "
        + "\"2025-04-30T12:00:00Z\", "
        + "\"status\": 404, \"error\": \"Not Found\", \"message\": \"Resource not found\", "
        + "\"errorCode\": \"RESOURCE_NOT_FOUND\", \"details\": "
        + "\"The requested resource was not found\", "
        + "\"path\": \"/api/resource\"}"
)
public class ErrorResponse {
    @Schema(
        description = "Unique identifier for the error",
        example = "123e4567-e89b-12d3-a456-426614174000"
    )
    String errorId;

    @Schema(
        description = "Timestamp of the error in ISO 8601 format",
        example = "2025-04-30T12:00:00Z"
    )
    Instant timestamp;

    @Schema(
        description = "HTTP status code of the error",
        example = "404",
        minimum = "300",
        maximum = "399"
    )
    int status;

    @Schema(
        description = "HTTP error name",
        example = "Not Found"
    )
    String error;

    @Schema(
        description = "Error message",
        example = "Resource not found"
    )
    String message;

    @Schema(
        description = "Error code for client-side error handling",
        example = "RESOURCE_NOT_FOUND"
    )
    String errorCode;

    @Schema(
        description = "Additional error details",
        example = "The requested resource was not found"
    )
    String details;

    @Schema(
        description = "Request path that generated this error",
        example = "/api/resource"
    )
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
