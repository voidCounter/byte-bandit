package lib.core.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(
    description = "Generic API response wrapper for successful requests",
    title = "ApiResponse",
    example = "{\"status\": 200, \"message\": \"Operation successful\", \"data\": {}, "
        + "\"timestamp\": \"2025-04-30T12:00:00Z\", \"path\": \"/endpoint\"}"
)
@Tag(name = "API Response")
public class ApiResponse<T> {
    @Schema(
        description = "HTTP status code of the response",
        example = "200",
        minimum = "100",
        maximum = "599"
    )
    private int status;

    @Schema(
        description = "Message describing the result of the operation",
        example = "Operation successful"
    )
    private String message;

    @Schema(
        description = "Data returned from the API",
        example = "{\"key\": \"value\"}"
    )
    private T data;

    @Schema(
        description = "Timestamp of the response in ISO 8601 format",
        example = "2025-04-30T12:00:00Z"
    )
    private String timestamp;

    @Schema(
        description = "Path of the request that generated this response",
        example = "/endpoint"
    )
    private String path;
}
