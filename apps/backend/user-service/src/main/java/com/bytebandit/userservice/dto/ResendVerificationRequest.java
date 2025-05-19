package com.bytebandit.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Request to resend email verification containing email address.")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResendVerificationRequest {

    @Schema(
        description = "The email address of the user for verification."
    )
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    String email;
}
