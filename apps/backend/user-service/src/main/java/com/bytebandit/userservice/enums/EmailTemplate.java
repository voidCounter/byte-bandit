package com.bytebandit.userservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EmailTemplate {

    REGISTRATION_CONFIRMATION("registration/registration-confirmation");

    private final String templatePath;

}
