package com.bytebandit.userservice.controller;

import com.bytebandit.userservice.dto.UserRegistrationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/register")
public class RegistrationController {
    @PostMapping
    ResponseEntity<String> register(@RequestBody UserRegistrationRequest req) {
        return ResponseEntity.ok("Registration successful");
    }
}
