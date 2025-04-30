package com.bytebandit.gateway.controller;

import com.bytebandit.gateway.dto.AuthenticatedUserDto;
import com.bytebandit.gateway.dto.LoginRequest;
import com.bytebandit.gateway.exception.GoogleLoginException;
import com.bytebandit.gateway.service.UserLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lib.core.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "User Authentication", description = "APIs for user authentication and session "
    + "management")
public class UserLoginController {
    
    private final UserLoginService userLoginService;
    @Value("${google.oauth.client-id}")
    private String googleClientId;
    
    @Value("${google.oauth.redirect-uri}")
    private String googleRedirectUri;
    
    @Value("${google.oauth.scope}")
    private String googleScope;
    
    
    @Value("${client.host.uri}")
    private String clientHostUri;

    @Operation(
        summary = "User login",
        description = "Authenticates a user based on their email or user ID and password. Returns "
            + "a success indicator and sets an authentication token in the response cookies."
    )
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
    @Operation(
        summary = "Get authenticated user",
        description = "Retrieves the information of the currently authenticated user. Returns "
            + "user details such as email, user ID, and roles."
    )
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthenticatedUserDto>> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(userLoginService.getAuthenticatedUser(authentication));
    }
    
    /**
     * Handles a GET request to initiate google login.
     *
     * @return ResponseEntity with a successful HTTP status.
     */
    @Operation(
        summary = "Google login",
        description = "Initiates Google login by generating an authorization URL. The user is "
            + "redirected to this URL to authorize the application."
    )
    @GetMapping("/google")
    public ResponseEntity<ApiResponse<String>> initiateGoogleLogin() {
        String authUrl = String.format(
            "https://accounts.google.com/o/oauth2/v2/auth?client_id=%s&redirect_uri=%s&response_type=code&scope=%s&access_type=offline",
            googleClientId, googleRedirectUri, googleScope
        );
        return ResponseEntity.ok(
            ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message("Google OAuth URL generated")
                .data(authUrl)
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .path("/api/v1/auth/google")
                .build()
        );
    }
    
    /**
     * Handles a GET request as Google callback after a user selects any Google account to log in.
     * This redirect-uri is provided in the authurl. Then Google request to this endpoint with code
     * if a user is authorized through Google consent screen, or with error if a user is
     * unauthorized through Google consent screen.
     *
     * @param code     code passed by Google for successful authorization.
     * @param error    error passed by Google when authorization fails.
     * @param response HTTPServlet response.
     *
     * @return ResponseEntity with a successful ApiResponse.
     */
    @Operation(
        summary = "Google login callback",
        description = "Handles the callback from Google after user authorization. Exchanges the "
            + "authorization code for an access token and logs in the user."
    )
    @GetMapping("/google/callback")
    public ResponseEntity<ApiResponse<Boolean>> handleGoogleCallback(
        @RequestParam(value = "code", required = false) String code,
        @RequestParam(value = "error", required = false) String error,
        HttpServletResponse response
    ) throws GoogleLoginException {
        if (error != null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                    ApiResponse.<Boolean>builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .message("Google OAuth error: " + error)
                        .data(Boolean.FALSE)
                        .timestamp(String.valueOf(System.currentTimeMillis()))
                        .path("/api/v1/auth/google/callback")
                        .build()
                );
        }
        // The code is single-use credential. It proves that the user authorized our application.
        ApiResponse<Boolean> apiResponse = userLoginService.handleGoogleLogin(code, response);
        if (apiResponse.getStatus() == HttpStatus.OK.value()) {
            return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, clientHostUri + "/app")
                .build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(
                ApiResponse.<Boolean>builder()
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("Google OAuth error: " + apiResponse.getMessage())
                    .data(Boolean.FALSE)
                    .timestamp(String.valueOf(System.currentTimeMillis()))
                    .path("/api/v1/auth/google/callback")
                    .build()
            );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Boolean>> logout(HttpServletRequest request,
                                                       HttpServletResponse response) {
        return ResponseEntity.ok().body(userLoginService.logout(request, response));
    }
}
