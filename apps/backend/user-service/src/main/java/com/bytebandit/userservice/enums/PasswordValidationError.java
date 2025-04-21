package com.bytebandit.userservice.enums;

/**
 * Enumeration of possible password validation errors. Used to provide specific feedback when
 * password validation fails.
 */
public enum PasswordValidationError {
    /** Password does not meet length requirements. */
    LENGTH_ERROR,
    /** Password is missing an uppercase letter. */
    MISSING_UPPERCASE,
    /** Password is missing a lowercase letter. */
    MISSING_LOWERCASE,
    /** Password is missing a digit. */
    MISSING_DIGIT,
    /** Password is missing a special character. */
    MISSING_SPECIAL_CHAR,
    /** Password contains the username. */
    CONTAINS_USERNAME,
    /** Password is a common or easily guessable password. */
    COMMON_PASSWORD
}