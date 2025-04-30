package com.bytebandit.gateway.controller;

import com.bytebandit.gateway.dto.AuthenticatedUserDto;
import com.bytebandit.gateway.dto.LoginRequest;
import com.bytebandit.gateway.exception.GoogleLoginException;
import com.bytebandit.gateway.service.UserLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lib.core.dto.response.ApiResponse;
import lib.core.dto.response.ErrorResponse;
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
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Login successful, access token set in cookies",
        content = @Content(
            schema = @Schema(implementation = ApiResponse.class),
            examples = @ExampleObject(
                name = "Successful Login",
                value = "{\"status\": 200, \"message\": \"Login successful\", \"data\": true, "
                    + "\"timestamp\": \"2025-04-30T12:00:00Z\", \"path\": \"/login\"}"
            )
        )
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "500",
        description = "Internal server error during login",
        content = @Content(
            examples = @ExampleObject(
                name = "Server Error",
                value = "{\"status\": 500, \"message\": \"Internal server error\", "
                    + "\"data\": false, \"timestamp\": \"2025-04-30T12:00:00Z\", "
                    + "\"path\": \"/login\"}"
            )
        )
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Invalid login credentials or malformed request",
        content = @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(
                name = "Invalid Input",
                value = "{\"status\": 400, \"error\": \"Bad Request\", \"message\": \"Invalid "
                    + "email format\", \"code\": \"INVALID_INPUT_FORMAT\", \"details\": "
                    + "\"Email must be a valid address\", \"path\": \"/login\", "
                    + "\"uuid\": \"123e4567-e89b-12d3-a456-426614174000\"}"
            )
        )
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "401",
        description = "User authentication failed",
        content = @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(
                name = "Authentication Failure",
                value = "{\"status\": 401, \"error\": \"Unauthorized\", \"message\": "
                    + "\"Invalid email or password\", \"code\": \"USER_NOT_AUTHENTICATED\", "
                    + "\"details\": \"Credentials do not match any user\", \"path\": "
                    + "\"/login\", \"uuid\": \"123e4567-e89b-12d3-a456-426614174000\"}"
            )
        )
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "500",
        description = "Internal server error during login",
        content = @Content(
            schema = @Schema(implementation = ErrorResponse.class),
            examples = @ExampleObject(
                name = "Server Error",
                value = "{\"status\": 500, \"error\": \"Internal Server Error\", \"message\": "
                    + "\"Unexpected error occurred\", \"code\": \"SERVER_ERROR\", \"details\": "
                    + "\"Contact support\", \"path\": \"/login\", \"uuid\": "
                    + "\"123e4567-e89b-12d3-a456-426614174000\"}"
            )
        )
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
}
