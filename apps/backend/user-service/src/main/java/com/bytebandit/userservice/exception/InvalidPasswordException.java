package com.bytebandit.userservice.exception;

import com.bytebandit.userservice.enums.PasswordValidationError;
import lombok.Getter;

/**
 * * EXCEPTION THROWN WHEN PASSWORD VALIDATION FAILS. * EXAMPLES: PASSWORD TOO SHORT, MISSING
 * REQUIRED CHARACTERS, ETC.
 */
@Getter
public class InvalidPasswordException extends RuntimeException {
    private final PasswordValidationError validationError;


    public InvalidPasswordException(String message) {
        this(message, null, null);
    }

    public InvalidPasswordException(String message, PasswordValidationError validationError) {
        super(message);
        this.validationError = validationError;
    }

    public InvalidPasswordException(String message, Throwable cause,
                                    PasswordValidationError validationError) {
        super(message, cause);
        this.validationError = validationError;
    }

}
