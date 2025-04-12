package com.bytebandit.userservice.controller;

import com.bytebandit.userservice.dto.ResendVerificationRequest;
import com.bytebandit.userservice.service.TokenVerificationService;
import com.bytebandit.userservice.service.UserRegistrationService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import lib.user.enums.TokenType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class EmailVerificationController {

    private final TokenVerificationService tokenVerificationService;
    private final UserRegistrationService userRegistrationService;

    @Value("${client.host.uri}")
    private String clientHostUri;

    /**
     * Verifies the email address of a user.
     *
     * @param token    The verification token.
     * @param userid   The user ID.
     * @param response The HTTP response.
     *
     * @return ResponseEntity with a redirect to the email verification confirmation page.
     */
    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(
        @RequestParam("token") @NotNull String token,
        @RequestParam("userid") @NotNull String userid,
        HttpServletResponse response
    ) {
        tokenVerificationService.verifyToken(
            token,
            userid,
            TokenType.EMAIL_VERIFICATION
        );
        try {
            response.sendRedirect(clientHostUri + "/email-verified");
            return ResponseEntity.status(HttpStatus.FOUND).build();
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                "Failed to redirect, please go to " + clientHostUri + "/login\n");
        }
    }

    /**
     * Resends the verification email to the user.
     *
     * @param resendVerificationRequest The request containing the email address.
     *
     * @return ResponseEntity with the status of the resend operation.
     */
    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerificationEmail(
        @RequestBody ResendVerificationRequest resendVerificationRequest
    ) {
        log.info("Resending verification email to: {}", resendVerificationRequest.getEmail());
        userRegistrationService.resendVerificationEmail(resendVerificationRequest.getEmail());
        return ResponseEntity.ok("Verification email resent successfully.");
    }
}
