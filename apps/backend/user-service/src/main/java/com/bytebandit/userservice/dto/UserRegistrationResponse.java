package com.bytebandit.userservice.dto;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * this will be changed with Global ResponseObject
 * if there is one [:)].
 */
public record UserRegistrationResponse(
        String fullName,
        String email
) {
}