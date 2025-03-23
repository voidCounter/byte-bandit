package com.bytebandit.userservice.controller;

import com.bytebandit.userservice.enums.EmailTemplate;
import com.bytebandit.userservice.enums.TokenType;
import com.bytebandit.userservice.service.TokenVerificationService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationController {

    private final TokenVerificationService tokenVerificationService;

    @GetMapping("/verify")
    public String verifyEmail(
            @RequestParam @NotNull String token,
            @RequestParam @NotNull String userid
    ) {
        boolean isValid = tokenVerificationService.verifyToken(
                token,
                userid,
                TokenType.EMAIL_VERIFICATION
        );
        return isValid
                    ? EmailTemplate.CONFIRMATION_SUCCESS.getTemplatePath()
                    : EmailTemplate.CONFIRMATION_FAILURE.getTemplatePath();
    }
}
