package com.bytebandit.userservice.controller;

import com.bytebandit.userservice.dto.UserRegistrationRequest;
import com.bytebandit.userservice.service.UserRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final UserRegistrationService service;

    @PostMapping("/register")
    public String register(
        @RequestBody UserRegistrationRequest request
    ) {
        service.register(request);
        return "User registered successfully";
    }
}
