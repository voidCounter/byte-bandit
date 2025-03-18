package com.bytebandit.userservice.dto;

import java.sql.Timestamp;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Response for user registration. */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationResponse {

    private UUID id;
    private String fullName;
    private String email;
    private boolean verified;
    private Timestamp createdAt;
}
