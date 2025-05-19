package com.bytebandit.gateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    description = "Details of the authenticated user, containing essential user information",
    title = "AuthenticatedUserDto",
    example = "{\"email\": \"john.doe@example.com\", \"fullName\": \"John Doe\"}"
)
public record AuthenticatedUserDto(
        @Schema(
            description = "Email address of the authenticated user",
            example = "john.doe@example.com",
            pattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        )
        String email,

        @Schema(
            description = "Full name of the authenticated user",
            example = "John Doe",
            minLength = 2,
            maxLength = 100
        )
        String fullName
) {
}

