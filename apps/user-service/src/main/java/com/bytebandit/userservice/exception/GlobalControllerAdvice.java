package com.bytebandit.userservice.exception;

import com.bytebandit.userservice.enums.EmailTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalControllerAdvice {
    @ExceptionHandler(FailedEmailVerificationAttemptException.class)
    public String handleFailedVerificationAttemptException(FailedEmailVerificationAttemptException ex) {
        return EmailTemplate.CONFIRMATION_FAILURE.getTemplatePath();
    }
}
