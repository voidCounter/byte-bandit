package com.bytebandit.gateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import lib.core.validation.ValidEmail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(
    description = "Request body for user login, containing authentication credentials",
    title = "LoginRequest",
    example = "{\"userId\": \"U123\", \"email\": \"user@example.com\", \"password\": "
        + "\"securePass123\"}"
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @Schema(
        description = "Optional user ID for login (either userId or email is required)",
        example = "U123",
        nullable = true,
        maxLength = 50
    )
    @Nullable
    private String userId;

    @Schema(
        description = "User password (minimum 8 characters, must include letters and numbers)",
        example = "securePass123",
        minLength = 8,
        maxLength = 100
    )
    private String password;

    @Schema(
        description = "User email address (must follow standard email format)",
        example = "user@example.com",
        pattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    )
    @ValidEmail
    private String email;
}
