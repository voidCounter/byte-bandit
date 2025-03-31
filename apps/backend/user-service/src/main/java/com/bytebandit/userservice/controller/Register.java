package com.bytebandit.userservice.controller;

import com.bytebandit.userservice.dto.UserRegistrationRequest;
import com.bytebandit.userservice.service.UserRegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Register {
    private final UserRegistrationService userRegistrationService;

    public Register(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
        @RequestBody UserRegistrationRequest userRegistrationRequest) {
        userRegistrationService.register(userRegistrationRequest);
        return ResponseEntity.ok("Registered");
    }
}
