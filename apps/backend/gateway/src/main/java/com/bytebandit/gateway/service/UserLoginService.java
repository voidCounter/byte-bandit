package com.bytebandit.gateway.service;

import com.bytebandit.gateway.dto.LoginRequest;
import com.bytebandit.gateway.enums.CookieKey;
import com.bytebandit.gateway.utils.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lib.core.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLoginService {

    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;

    @Value("${app.access-token-expiration}")
    private long accessTokenExpirationInSeconds;

    /**
     * Authenticates the user and generates an access token.
     *
     * @param loginRequest the login request containing email and password
     * @param httpResponse the HTTP response to set the access token cookie
     * @return ApiResponse indicating success or failure
     */
    public ApiResponse<Boolean> login(
        LoginRequest loginRequest,
        HttpServletResponse httpResponse
    ) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        if (loginRequest.getUserId() == null) {
            loginRequest.setUserId(resolveUserId(loginRequest));
        }

        CookieUtil.setCookie(
            httpResponse,
            CookieKey.ACCESS_TOKEN.getKey(),
            tokenService.generateToken(userDetails, accessTokenExpirationInSeconds,
                UUID.fromString(loginRequest.getUserId())),
            false,
            7 * 24 * 60 * 60,
            "/",
            false
        );
        return ApiResponse.<Boolean>builder()
            .status(HttpStatus.OK.value())
            .message("Login successful")
            .data(Boolean.TRUE)
            .timestamp(
                String.valueOf(System.currentTimeMillis())
            )
            .path("/api/v1/auth/login")
            .build();

    }

    private String resolveUserId(LoginRequest loginRequest) {
        return customUserDetailsService.loadUserByUsername(
            loginRequest.getEmail()
        ).getId().toString();
    }
}
