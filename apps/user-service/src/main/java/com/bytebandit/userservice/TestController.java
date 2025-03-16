package com.bytebandit.userservice;

import com.bytebandit.userservice.dto.UserRegistrationRequest;
import com.bytebandit.userservice.dto.UserRegistrationResponse;
import com.bytebandit.userservice.service.UserRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class TestController {

    private final UserRegistrationService userRegistrationService;

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponse> register(
            @RequestBody @Validated UserRegistrationRequest userRegistrationRequest
    ) {
        return ResponseEntity.ok(
            userRegistrationService.register(userRegistrationRequest)
        );
    }

}

