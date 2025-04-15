package com.bytebandit.gateway.service;

import com.bytebandit.gateway.dto.AuthenticatedUserDto;
import com.bytebandit.gateway.dto.LoginRequest;
import com.bytebandit.gateway.enums.CookieKey;
import com.bytebandit.gateway.exception.UserNotAuthenticatedException;
import com.bytebandit.gateway.utils.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lib.core.dto.response.ApiResponse;
import lib.user.model.UserEntityTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserLoginService {
    
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    
    @Value("${app.access-token-expiration}")
    private long accessTokenExpirationInSeconds;
    
    @Value("${app.refresh-token-expiration}")
    private long refreshTokenExpirationInSeconds;
    
    /**
     * Authenticates the user and generates an access token.
     *
     * @param loginRequest the login request containing email and password
     * @param httpResponse the HTTP response to set the access token cookie
     *
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
        
        if (authentication.getPrincipal() == null) {
            throw new UserNotAuthenticatedException("User not authenticated");
        }
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        if (loginRequest.getUserId() == null) {
            loginRequest.setUserId(resolveUserId(loginRequest));
        } else {
            try {
                UUID.fromString(loginRequest.getUserId());
            } catch (IllegalArgumentException e) {
                loginRequest.setUserId(
                    resolveUserId(loginRequest)
                );
            }
        }
        
        String accessToken = tokenService.generateToken(
            userDetails,
            accessTokenExpirationInSeconds,
            UUID.fromString(loginRequest.getUserId())
        );
        
        CookieUtil.setCookie(
            httpResponse,
            CookieKey.ACCESS_TOKEN.getKey(),
            accessToken,
            true,
            7 * 24 * 60 * 60,
            "/",
            false
        );
        
        tokenService.generateAndSaveRefreshToken(
            userDetails,
            refreshTokenExpirationInSeconds,
            UUID.fromString(loginRequest.getUserId())
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
    
    /**
     * Retrieves the authenticated user's information.
     *
     * @param authentication the authentication object containing the user's authentication
     *
     * @return AuthenticatedUserDto containing the user's information
     */
    public ApiResponse<AuthenticatedUserDto> getAuthenticatedUser(
        Authentication authentication
    ) {
        if (authentication == null) {
            // Handle anonymous authentication appropriately,
            // e.g., throw a custom exception or return an error response.
            throw new UserNotAuthenticatedException("User not authenticated");
        }
        try {
            UserEntityTemplate user = (UserEntityTemplate) authentication.getPrincipal();
            return ApiResponse.<AuthenticatedUserDto>builder()
                .status(HttpStatus.OK.value())
                .message("Authenticated user confirmed.")
                .data(
                    new AuthenticatedUserDto(
                        user.getEmail(),
                        user.getFullName()
                    )
                )
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .path("/api/v1/auth/me")
                .build();
        } catch (Exception exception) {
            throw new UserNotAuthenticatedException("User not authenticated");
        }
    }
    
    private String resolveUserId(LoginRequest loginRequest) {
        return customUserDetailsService.loadUserByUsername(
            loginRequest.getEmail()
        ).getId().toString();
    }
}
