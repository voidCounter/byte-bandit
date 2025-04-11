package com.bytebandit.userservice.controller;

import com.bytebandit.userservice.service.TokenVerificationService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import lib.user.enums.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailVerificationController {

    private final TokenVerificationService tokenVerificationService;

    @Value("${client.host.uri}")
    private String clientHostUri;

    /**
     * Verifies the email address of a user.
     *
     * @param token    The verification token.
     * @param userid   The user ID.
     * @param response The HTTP response.
     *
     * @return ResponseEntity with a redirect to the login page.
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
}
