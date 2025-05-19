package com.bytebandit.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import lib.core.validation.ValidEmail;
import lib.core.validation.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * DTO for user registration request.
 */
@Schema(
    description = "Request to register a new user."
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationRequest {

    @Schema(
        description = "The username of the user. Must be unique and less than 20 characters.",
        example = "john_doe",
        maxLength = 20
    )
    @Nullable
    @Size(max = 20, message = "Username must be less than 20 characters")
    private String fullName;

    @ValidEmail
    private String email;

    @ValidPassword
    private String password;
}
