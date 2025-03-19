package com.bytebandit.userservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EmailTemplate {

    REGISTRATION_CONFIRMATION("registration/registration-confirmation"),
    CONFIRMATION_SUCCESS("registration/confirmation-success"),
    CONFIRMATION_FAILURE("registration/confirmation-failure");

    private final String templatePath;

}
