package com.bytebandit.userservice.controller;

import com.bytebandit.userservice.dto.UserRegistrationRequest;
import com.bytebandit.userservice.service.UserRegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {
    private final UserRegistrationService userRegistrationService;

    public RegistrationController(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }

    /**
     * Registers a new user.
     *
     * @param request The user registration request.
     *
     * @return ResponseEntity with the registration status.
     */

    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody UserRegistrationRequest request) {
        userRegistrationService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }
}
