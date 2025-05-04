package com.bytebandit.userservice.controller;

import com.bytebandit.userservice.dto.UserRegistrationRequest;
import com.bytebandit.userservice.dto.UserRegistrationResponse;
import com.bytebandit.userservice.service.UserRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lib.core.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "User Registration", description = "User registration related endpoints.")
public class UserRegistrationController {
    private final UserRegistrationService userRegistrationService;
    
    public UserRegistrationController(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }
    
    /**
     * Registers a new user.
     *
     * @param request The user registration request.
     *
     * @return ResponseEntity with the registration status.
     */

    @Operation(
        summary = "User registration",
        description = "Registers a new user with the provided details."
    )
    @PostMapping("/register")
    ResponseEntity<ApiResponse<UserRegistrationResponse>> register(
        @Valid @RequestBody UserRegistrationRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.<UserRegistrationResponse>builder()
            .data(userRegistrationService.register(request))
            .message("User registered successfully")
            .timestamp(java.time.Instant.now().toString())
            .status(200).build());
    }
}
