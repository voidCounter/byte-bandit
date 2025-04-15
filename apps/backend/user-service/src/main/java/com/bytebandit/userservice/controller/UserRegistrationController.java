package com.bytebandit.userservice.controller;

import com.bytebandit.userservice.dto.UserRegistrationRequest;
import com.bytebandit.userservice.dto.UserRegistrationResponse;
import com.bytebandit.userservice.service.UserRegistrationService;
import jakarta.validation.Valid;
import lib.core.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Timer;

@RestController
public class UserRegistrationController {
    private final UserRegistrationService userRegistrationService;

    public UserRegistrationController(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }

    /**
     * Registers a new user.
     *
     * @param request The user registration request.
     * @return ResponseEntity with the registration status.
     */

    @PostMapping("/register")
    ResponseEntity<?> register(@Valid @RequestBody UserRegistrationRequest request) {
        return ResponseEntity.ok(ApiResponse.<UserRegistrationResponse>builder().
                data(userRegistrationService.register(request)).
                message("User registered successfully").
                timestamp(Timestamp.from(java.time.Instant.now()).toString()).
                status(200).
                build());
    }
}
