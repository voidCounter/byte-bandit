package com.bytebandit.userservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TokenType {

    EMAIL_VERIFICATION("email-verification"),
    PASSWORD_RESET("password-reset"),
    REFRESH("refresh");

    private final String token;
}
