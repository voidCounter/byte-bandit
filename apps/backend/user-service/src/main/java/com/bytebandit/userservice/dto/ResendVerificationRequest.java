package com.bytebandit.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResendVerificationRequest {
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    String email;
}
