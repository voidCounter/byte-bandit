package com.bytebandit.userservice.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import lib.core.validation.ValidEmail;
import lib.core.validation.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user registration request.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationRequest {
    @Nullable
    @Size(max = 20, message = "Username must be less than 20 characters")
    private String fullName;

    @ValidEmail
    private String email;

    @ValidPassword
    private String password;
}
