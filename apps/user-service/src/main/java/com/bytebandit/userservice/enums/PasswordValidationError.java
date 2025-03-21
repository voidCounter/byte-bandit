package com.bytebandit.userservice.enums;

public enum PasswordValidationError {
    LENGTH_ERROR,
    MISSING_UPPERCASE,
    MISSING_LOWERCASE,
    MISSING_DIGIT,
    MISSING_SPECIAL_CHAR,
    CONTAINS_USERNAME,
    COMMON_PASSWORD
}
