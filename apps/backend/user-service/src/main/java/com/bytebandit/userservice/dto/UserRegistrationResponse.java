package com.bytebandit.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * this will be changed with Global ResponseObject
 * if there is one [:)].
 */
@Schema(description = "User registration response")
public record UserRegistrationResponse(
        String fullName,
        String email
) {
}