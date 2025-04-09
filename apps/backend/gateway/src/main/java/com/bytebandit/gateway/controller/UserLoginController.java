package com.bytebandit.gateway.controller;

import com.bytebandit.gateway.dto.LoginRequest;
import com.bytebandit.gateway.service.UserLoginService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lib.core.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserLoginController {

    private final UserLoginService userLoginService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Boolean>> login(
        @Valid @RequestBody LoginRequest loginRequest,
        HttpServletResponse response
    ) {
        return ResponseEntity.ok(userLoginService.login(loginRequest, response));
    }
}
