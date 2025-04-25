package com.bytebandit.gateway.controller;

import com.bytebandit.gateway.dto.AuthenticatedUserDto;
import com.bytebandit.gateway.dto.LoginRequest;
import com.bytebandit.gateway.service.UserLoginService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lib.core.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
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
    
    /**
     * Handles a GET request to retrieve the authenticated user's information.
     *
     * @return ResponseEntity with a successful HTTP status.
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthenticatedUserDto>> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(userLoginService.getAuthenticatedUser(authentication));
    }
}
