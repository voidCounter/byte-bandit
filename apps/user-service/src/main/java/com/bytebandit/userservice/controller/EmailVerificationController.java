package com.bytebandit.userservice.controller;

import com.bytebandit.userservice.enums.TokenType;
import com.bytebandit.userservice.service.TokenVerificationService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class EmailVerificationController {

    private final TokenVerificationService tokenVerificationService;

    @Value("${client.host.uri}")
    private String clientHostUri;

    @GetMapping("/verify")
    public ResponseEntity<Void> verifyEmail(
            @RequestParam @NotNull String token,
            @RequestParam @NotNull String userid,
            HttpServletResponse response
    ) {
        tokenVerificationService.verifyToken(
                token,
                userid,
                TokenType.EMAIL_VERIFICATION
        );
        try {
            response.sendRedirect(clientHostUri + "/login");
        } catch (IOException ex) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    "Failed to redirect, please go to " + clientHostUri + "/login\n" +
                            "cause: " + ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.FOUND).build();
    }
}
