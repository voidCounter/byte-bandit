package com.bytebandit.gateway.service;

import com.bytebandit.gateway.clients.UserServiceClient;
import com.bytebandit.gateway.dto.AuthenticatedUserDto;
import com.bytebandit.gateway.dto.GoogleTokenResponse;
import com.bytebandit.gateway.dto.LoginRequest;
import com.bytebandit.gateway.enums.CookieKey;
import com.bytebandit.gateway.exception.EmailAlreadyUsedWithGoogleAccountException;
import com.bytebandit.gateway.exception.GoogleLoginException;
import com.bytebandit.gateway.exception.UserNotAuthenticatedException;
import com.bytebandit.gateway.model.UserEntity;
import com.bytebandit.gateway.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import lib.core.dto.response.ApiResponse;
import lib.core.enums.UserAction;
import lib.core.events.UserEvent;
import lib.core.utils.CookieUtil;
import lib.user.model.UserEntityTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserLoginService {
    
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final UserServiceClient userServiceClient;
    
    @Value("${app.access-token-expiration}")
    private long accessTokenExpirationInSeconds;
    
    @Value("${app.refresh-token-expiration}")
    private long refreshTokenExpirationInSeconds;
    
    @Value("${google.oauth.client-id}")
    private String googleClientId;
    
    @Value("${google.oauth.client-secret}")
    private String googleClientSecret;
    
    @Value("${google.oauth.redirect-uri}")
    private String googleRedirectUri;
    
    @Value("${google.oauth.token-endpoint}")
    private String googleTokenEndpoint;
    
    @Value("${google.oauth.userinfo-endpoint}")
    private String googleUserInfoEndpoint;
    
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
        Optional<UserEntity> userEntity = userRepository.findByEmail(loginRequest.getEmail());
        if (userEntity.isPresent() && userEntity.get().getOauthId() != null) {
            throw new EmailAlreadyUsedWithGoogleAccountException();
        }
        
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
        
        generateAndSetTokens(userDetails, loginRequest.getUserId(), httpResponse);
        
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
    
    /**
     * Handles the Google login process.
     *
     * @param code     the authorization code received from Google.
     * @param response the HTTP response to set the access token cookie.
     *
     * @return ApiResponse indicating success or failure.
     */
    public ApiResponse<Boolean> handleGoogleLogin(String code, HttpServletResponse response) {
        
        try {
            GoogleIdToken.Payload payload = verifyAndExtractPayload(code);
            String email = payload.getEmail();
            String fullName = (String) payload.get("name");
            String oauthId = payload.get("sub").toString();
            log.debug("oauthid: {}", oauthId);
            
            UserEntity user = findOrCreateUser(email, fullName, oauthId);
            // if a user is not verified, verify them
            if (!user.isVerified()) {
                user.setVerified(true);
                userServiceClient.createUserEvent(
                    UserEvent.builder().userId(user.getId()).email(user.getEmail()).action(
                        UserAction.USER_VERIFIED).build());
            }
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
            generateAndSetTokens(userDetails, user.getId().toString(), response);
            
            return ApiResponse.<Boolean>builder()
                .status(HttpStatus.OK.value())
                .message("Google login successful")
                .data(Boolean.TRUE)
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .path("/api/v1/auth/google/callback")
                .build();
            
        } catch (GoogleLoginException e) {
            log.error("Google OAuth error", e);
            return ApiResponse.<Boolean>builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message("Google OAuth error: " + e.getMessage())
                .data(Boolean.FALSE)
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .path("/api/v1/auth/google/callback")
                .build();
        }
    }
    
    
    private GoogleIdToken.Payload verifyAndExtractPayload(String code)
        throws GoogleLoginException {
        GoogleTokenResponse tokenResponse = exchangeCodeForToken(code);
        GoogleIdToken googleIdToken = verifyIdToken(tokenResponse.getIdToken());
        return googleIdToken.getPayload();
    }
    
    private GoogleTokenResponse exchangeCodeForToken(String code) {
        String tokenRequestBody = String.format(
            "code=%s&client_id=%s&client_secret=%s&redirect_uri=%s&grant_type=authorization_code",
            code, googleClientId, googleClientSecret, googleRedirectUri
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> request = new HttpEntity<>(tokenRequestBody, headers);
        
        ResponseEntity<GoogleTokenResponse> tokenResponse;
        try {
            tokenResponse = restTemplate.postForEntity(
                googleTokenEndpoint, request, GoogleTokenResponse.class
            );
        } catch (RestClientException e) {
            throw new GoogleLoginException(
                "Failed to exchange authorization code: " + e.getMessage());
        }
        
        if (tokenResponse.getStatusCode() != HttpStatus.OK || tokenResponse.getBody() == null) {
            log.error("Token endpoint returned non-OK status: {}",
                tokenResponse.getStatusCode());
            throw new GoogleLoginException("Failed to retrieve authorization code.");
        }
        
        return tokenResponse.getBody();
    }
    
    private UserEntity findOrCreateUser(String email, String fullName, String oauthId) {
        return userRepository.findByEmail(email)
            .orElseGet(() -> {
                UserEntity newUser = new UserEntity();
                newUser.setEmail(email);
                newUser.setFullName(fullName);
                newUser.setOauthId(oauthId);
                newUser.setVerified(false);
                return userRepository.save(newUser);
            });
    }
    
    private GoogleIdToken verifyIdToken(String idToken) {
        GoogleIdTokenVerifier verifier =
            new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();
        GoogleIdToken googleIdToken;
        try {
            googleIdToken = verifier.verify(idToken);
        } catch (GeneralSecurityException | IOException e) {
            throw new GoogleLoginException("Invalid ID token: " + e.getMessage());
        }
        
        if (googleIdToken == null) {
            throw new GoogleLoginException("Invalid ID token.");
        }
        return googleIdToken;
    }
    
    private void generateAndSetTokens(UserDetails userDetails, String userId,
                                      HttpServletResponse httpResponse) {
        String accessToken = tokenService.generateToken(
            userDetails,
            accessTokenExpirationInSeconds,
            UUID.fromString(userId)
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
            UUID.fromString(userId)
        );
        
        // test this.
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
