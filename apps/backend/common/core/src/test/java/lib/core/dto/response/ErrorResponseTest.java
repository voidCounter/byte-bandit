package lib.core.dto.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

@SpringBootTest
class ErrorResponseTest {

    private UUID fixedId;
    private Supplier<UUID> uuidSupplier;
    private Clock fixedClock;

    /**
     * This UUID is used to ensure that the generated UUID is consistent across tests.
     * In a real-world scenario, you would use a UUID generator or a random UUID.
     */
    @BeforeEach
    void setUp() {
        fixedId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        uuidSupplier = () -> fixedId;

        fixedClock = Clock.fixed(Instant.parse("2025-04-09T10:15:30.00Z"), ZoneOffset.UTC);
    }

    /**
     * This test checks that the ErrorResponse object is created correctly with all fields set.
     * It uses a fixed UUID and a fixed clock to ensure that the test is deterministic.
     */
    @Test
    void testCreate_shouldReturnValidErrorResponse() {
        HttpStatus status = HttpStatus.NOT_FOUND;
        String error = "Not Found";
        String message = "Resource not found";
        String errorCode = "404_NOT_FOUND";
        String details = "Item with ID 123 does not exist";
        String path = "/api/items/123";

        Instant before = Instant.now(fixedClock);
        ErrorResponse response = ErrorResponse.builder()
            .errorId(uuidSupplier.get().toString())
            .timestamp(before)
            .status(status.value())
            .error(error)
            .message(message)
            .errorCode(errorCode)
            .details(details)
            .path(path)
            .build();

        assertNotNull(response);
        assertEquals(fixedId.toString(), response.getErrorId());
        assertEquals(before, response.getTimestamp());
        assertEquals(status.value(), response.getStatus());
        assertEquals(error, response.getError());
        assertEquals(message, response.getMessage());
        assertEquals(errorCode, response.getErrorCode());
        assertEquals(details, response.getDetails());
        assertEquals(path, response.getPath());
    }

    /**
     * This test checks that the ErrorResponse object is created correctly using the static factory
     * method. It uses a fixed UUID and a fixed clock to ensure that the test is deterministic.
     */
    @Test
    void testCreate_staticFactoryMethod_shouldSetAllFieldsCorrectly() {
        // Given
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String error = "Bad Request";
        String message = "Invalid input provided";
        String errorCode = "400_INVALID";
        String details = "Field 'email' must be a valid email";
        String path = "/api/users";
        Supplier<UUID> customIdSupplier = () -> fixedId;

        ErrorResponse response = ErrorResponse.create(
            status,
            error,
            message,
            errorCode,
            details,
            path,
            customIdSupplier
        );

        assertNotNull(response);
        assertEquals(fixedId.toString(), response.getErrorId());
        assertNotNull(response.getTimestamp());
        assertEquals(status.value(), response.getStatus());
        assertEquals(error, response.getError());
        assertEquals(message, response.getMessage());
        assertEquals(errorCode, response.getErrorCode());
        assertEquals(details, response.getDetails());
        assertEquals(path, response.getPath());

        Instant now = Instant.now();
        assertTrue(response.getTimestamp().isBefore(now.plusSeconds(1)));
    }
}
