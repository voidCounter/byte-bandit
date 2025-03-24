package com.bytebandit.userservice.exception;

import com.bytebandit.userservice.enums.EmailTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;


@ControllerAdvice
public class GlobalControllerAdvice {

    @Value("${client.host.uri}")
    private String clientHostUri;

    @ExceptionHandler(FailedEmailVerificationAttemptException.class)
    public ModelAndView handleFailedVerificationAttemptException(
            FailedEmailVerificationAttemptException ex
    ) {
        ModelAndView model = new ModelAndView(EmailTemplate.CONFIRMATION_FAILURE.getTemplatePath());
        model.addObject("message", ex.getMessage());
        model.addObject("resendUrl", clientHostUri + "/email-verification");
        return model;
    }
}
