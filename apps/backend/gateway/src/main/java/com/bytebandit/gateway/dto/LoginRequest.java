package com.bytebandit.gateway.dto;

import jakarta.annotation.Nullable;
import lib.core.validation.ValidEmail;
import lib.core.validation.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @Nullable
    private String userId;

    private String password;

    @ValidEmail
    private String email;
}
