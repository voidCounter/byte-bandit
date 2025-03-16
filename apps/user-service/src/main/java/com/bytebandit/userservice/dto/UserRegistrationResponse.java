package com.bytebandit.userservice.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

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
