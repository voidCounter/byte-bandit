package lib.user.enums;

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