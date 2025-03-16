package com.bytebandit.userservice.dto;

import com.bytebandit.userservice.annotation.ValidEmail;
import com.bytebandit.userservice.annotation.ValidPassword;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;

@Data
@Getter
@Setter
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
