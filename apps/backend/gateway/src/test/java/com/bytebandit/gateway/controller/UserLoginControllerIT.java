package com.bytebandit.gateway.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bytebandit.gateway.clients.UserServiceClient;
import com.bytebandit.gateway.configurer.AbstractPostgresContainer;
import com.bytebandit.gateway.dto.GoogleTokenResponse;
import com.bytebandit.gateway.model.TokenEntity;
import com.bytebandit.gateway.model.UserEntity;
import com.bytebandit.gateway.repository.TokenRepository;
import com.bytebandit.gateway.repository.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import io.restassured.RestAssured;
import io.restassured.config.RedirectConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.UUID;
import lib.core.events.UserEvent;
import lib.user.enums.TokenType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@Import({BCryptPasswordEncoder.class})
@Slf4j
class UserLoginControllerIT extends AbstractPostgresContainer {
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private TokenRepository tokenRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @MockBean
    private RestTemplate restTemplate;
    
    @MockBean
    private GoogleIdTokenVerifier googleIdTokenVerifier;
    
    @MockBean
    private UserServiceClient userServiceClient;
    
    // Test specific properties(from application-test.yml)
    @Value("${google.oauth.token-endpoint}")
    private String googleTokenEndpoint;
    
    @Value("${client.host.uri}")
    private String clientHostUri;
    
    private static final String LOGIN_PATH = "/api/v1/auth/login";
    private static final String ME_PATH = "/api/v1/auth/me";
    private static final String CSRF_PATH = "/api/v1/auth/csrf";
    private static final String GOOGLE_CALLBACK_PATH = "/api/v1/auth/google/callback";
    private static final String LOGOUT_PATH = "/api/v1/auth/logout";
    private final UUID regularUserId = UUID.randomUUID();
    private final String regularUserEmail = "test@example.com";
    private final String regularUserPassword = "ValidPass$1";
    private String csrfToken;
    
    /**
     * Sets up the test environment before each test. Configures REST Assured port and fetches CSRF
     * token. Cleans the repository and creates a standard user for login tests.
     */
    @BeforeEach
    void setup() {
        RestAssured.port = port;
        
        // configure rest assured not to automatically follow redirects for Google tests
        RestAssured.config = RestAssuredConfig.config()
            .redirect(RedirectConfig.redirectConfig().followRedirects(false));
        
        try {
            Response response = RestAssured
                .given()
                .when()
                .get(CSRF_PATH)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .response();
            csrfToken = response.getCookies().get("XSRF-TOKEN");
            log.info("CSRF Token fetched: {}", csrfToken);
        } catch (Exception e) {
            log.error("Failed to fetch CSRF token from {}. CSRF tests might fail.", CSRF_PATH, e);
            csrfToken = null;
        }
        
        
        userRepository.deleteAll();
        
        UserEntity user = new UserEntity();
        user.setId(regularUserId);
        user.setEmail(regularUserEmail);
        user.setFullName("test user");
        user.setPasswordHash(passwordEncoder.encode(regularUserPassword));
        user.setVerified(true);
        userRepository.save(user);
        log.info("Test setup complete. Regular user created with email: {}", regularUserEmail);
    }
    
    /**
     * Creates a standard RequestSpecification including CSRF token for POST requests.
     *
     * @return RequestSpecification
     */
    private RequestSpecification requestSpecification() {
        RequestSpecification spec = RestAssured.given()
            .contentType(ContentType.JSON);
        if (csrfToken != null) {
            spec = spec.header("X-XSRF-TOKEN", csrfToken)
                .cookie("XSRF-TOKEN", csrfToken);
        } else {
            log.warn("CSRF token is null. POST requests requiring CSRF might fail.");
        }
        return spec;
    }
    
    
    /**
     * Tests the login endpoint with valid credentials.
     */
    @Test
    @DisplayName("POST /login - Success with valid credentials")
    void login_shouldSucceedWithValidCredentials() {
        String jsonBody = String.format("""
            {
                "email": "%s",
                "password": "%s"
            }
            """, regularUserEmail, regularUserPassword);
        
        requestSpecification()
            .body(jsonBody)
            .when()
            .post(LOGIN_PATH)
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.OK.value())
            .body("status", equalTo(200))
            .body("message", equalTo("Login successful"))
            .body("data", equalTo(true))
            .body("timestamp", notNullValue())
            .body("path", equalTo(LOGIN_PATH))
            .cookie("access_token", notNullValue());
    }
    
    /**
     * Tests the login endpoint with invalid credentials.
     */
    @Test
    @DisplayName("POST /login - Failure with invalid password")
    void login_shouldFailWithInvalidPassword() {
        String jsonBody = String.format("""
            {
                "email": "%s",
                "password": "InvalidPassword"
            }
            """, regularUserEmail);
        
        requestSpecification()
            .body(jsonBody)
            .when()
            .post(LOGIN_PATH)
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .body("status", equalTo(401))
            .body("message", containsString("Bad credentials"))
            .body("path", equalTo(LOGIN_PATH));
    }
    
    /**
     * Tests the login endpoint with an invalid email.
     */
    @Test
    @DisplayName("GET /me - Success after login")
    void authenticationConfirmation_shouldSucceedAfterLogin() {
        String jsonBody = String.format("""
            {
                "email": "%s",
                "password": "%s"
            }
            """, regularUserEmail, regularUserPassword);
        
        // Login to get cookies
        Response loginResponse = requestSpecification()
            .body(jsonBody)
            .when()
            .post(LOGIN_PATH)
            .then()
            .log().all()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .response();
        
        Map<String, String> cookies = loginResponse.getCookies();
        
        // Call /me with the cookies
        given()
            .cookies(cookies)
            .when()
            .get(ME_PATH)
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.OK.value())
            .body("status", equalTo(200))
            .body("message", equalTo("Authenticated user confirmed."))
            .body("data.email", equalTo(regularUserEmail))
            .body("data.fullName", equalTo("test user"))
            .body("timestamp", notNullValue())
            .body("path", equalTo(ME_PATH));
    }
    
    
    /**
     * Tests the CSRF token endpoint.
     *
     * @throws GeneralSecurityException GeneralSecurityException
     * @throws IOException              IOException
     */
    @Test
    @DisplayName("GET /google/callback - Success with Code (New User) - Expect Redirect")
    void googleCallback_SuccessNewUser_ShouldRedirect()
        throws GeneralSecurityException, IOException {
        String newUserEmail = "new.google.user@example.com";
        String newUserOauthId = "google-oauth-id-new";
        String dummyIdToken = "dummy-id-token-for-" + newUserEmail;
        
        // Mock RestTemplate response for token exchange
        GoogleTokenResponse tokenResponse = new GoogleTokenResponse();
        tokenResponse.setIdToken(dummyIdToken);
        ResponseEntity<GoogleTokenResponse> googleResponseEntity =
            new ResponseEntity<>(tokenResponse, HttpStatus.OK);
        
        when(restTemplate.postForEntity(
            eq(googleTokenEndpoint),
            any(HttpEntity.class),
            eq(GoogleTokenResponse.class)))
            .thenReturn(googleResponseEntity);
        
        String newUserName = "New Google User";
        GoogleIdToken.Payload mockPayload = new GoogleIdToken.Payload();
        mockPayload.setSubject(newUserOauthId);
        mockPayload.setEmail(newUserEmail);
        mockPayload.setEmailVerified(true);
        mockPayload.set("name", newUserName);
        GoogleIdToken mockIdToken = mock(GoogleIdToken.class);
        when(mockIdToken.getPayload()).thenReturn(mockPayload);
        
        // it should return mockIdToken, simulating successful verification.
        when(googleIdTokenVerifier.verify(anyString())).thenReturn(mockIdToken);
        
        
        String testCode = "valid-google-auth-code-new-user";
        String expectedRedirectUrl = clientHostUri + "/app";
        given()
            .queryParam("code", testCode)
            .config(RestAssured.config().redirect(
                RedirectConfig.redirectConfig().followRedirects(false)))
            .log().all()
            .when()
            .get(GOOGLE_CALLBACK_PATH)
            .then()
            .log().all()
            .statusCode(HttpStatus.FOUND.value()) // expect http 302 due to redirect
            .header("Location", is(expectedRedirectUrl));
        
        verify(restTemplate).postForEntity(eq(googleTokenEndpoint), any(HttpEntity.class),
            eq(GoogleTokenResponse.class));
        verify(userServiceClient).createUserEvent(any(UserEvent.class));
    }
    
    
    /**
     * Tests the CSRF token endpoint with an invalid email.
     *
     * @throws GeneralSecurityException GeneralSecurityException
     * @throws IOException              IOException
     */
    @Test
    @DisplayName("GET /google/callback - Success with Code (Existing User) - Expect Redirect")
    void googleCallback_SuccessExistingUser_ShouldRedirect()
        throws GeneralSecurityException, IOException {
        String existingUserEmail = "existing.google.user@example.com";
        String existingUserName = "Existing Google User";
        String existingUserOauthId = "google-oauth-id-existing";
        UUID existingUserId = UUID.randomUUID();
        
        // Create the existing Google user in the DB
        UserEntity existingGoogleUser = new UserEntity();
        existingGoogleUser.setId(existingUserId);
        existingGoogleUser.setEmail(existingUserEmail);
        existingGoogleUser.setFullName(existingUserName);
        existingGoogleUser.setOauthId(existingUserOauthId);
        existingGoogleUser.setVerified(true);
        userRepository.save(existingGoogleUser);
        
        String dummyIdToken = "dummy-id-token-for-" + existingUserEmail;
        GoogleTokenResponse tokenResponse = new GoogleTokenResponse();
        tokenResponse.setIdToken(dummyIdToken);
        ResponseEntity<GoogleTokenResponse> googleResponseEntity =
            new ResponseEntity<>(tokenResponse, HttpStatus.OK);
        
        when(restTemplate.postForEntity(
            eq(googleTokenEndpoint),
            any(HttpEntity.class),
            eq(GoogleTokenResponse.class)))
            .thenReturn(googleResponseEntity);
        
        GoogleIdToken.Payload mockPayload = new GoogleIdToken.Payload();
        mockPayload.setSubject(existingUserOauthId);
        mockPayload.setEmail(existingUserEmail);
        mockPayload.setEmailVerified(true);
        mockPayload.set("name", existingUserName);
        GoogleIdToken mockIdToken = mock(GoogleIdToken.class);
        when(mockIdToken.getPayload()).thenReturn(mockPayload);
        
        when(googleIdTokenVerifier.verify(anyString())).thenReturn(mockIdToken);
        
        String expectedRedirectUrl = clientHostUri + "/app";
        String testCode = "valid-google-auth-code-existing-user";
        given()
            .queryParam("code", testCode)
            .config(RestAssured.config()
                .redirect(RedirectConfig.redirectConfig().followRedirects(false)))
            .log().all()
            .when()
            .get(GOOGLE_CALLBACK_PATH)
            .then()
            .log().all()
            .statusCode(HttpStatus.FOUND.value())
            .header("Location", is(expectedRedirectUrl));
        
        verify(restTemplate).postForEntity(eq(googleTokenEndpoint), any(HttpEntity.class),
            eq(GoogleTokenResponse.class));
        verify(userServiceClient, never()).createUserEvent(any(UserEvent.class));
    }
    
    
    /**
     * Tests the Google callback endpoint with an error parameter.
     */
    @Test
    @DisplayName("GET /google/callback - Google Error Parameter - Expect Unauthorized")
    void googleCallback_WithErrorParameter_ShouldReturnUnauthorized() {
        String googleError = "access_denied";
        
        given()
            .queryParam("error", googleError)
            .config(RestAssured.config().redirect(RedirectConfig.redirectConfig()
                .followRedirects(true)))
            .log().all()
            .when()
            .get(GOOGLE_CALLBACK_PATH)
            .then()
            .log().all()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .body("status", equalTo(HttpStatus.UNAUTHORIZED.value()))
            .body("message", equalTo("Google OAuth error: " + googleError))
            .body("data", equalTo(false))
            .body("path", equalTo(GOOGLE_CALLBACK_PATH))
            .body("timestamp", notNullValue());
        
        verify(restTemplate, never()).postForEntity(any(), any(), any());
    }
    
    /**
     * Tests the Google callback endpoint with an invalid code.
     */
    @Test
    @DisplayName("GET /google/callback - Code Exchange Failure - Expect OK with Error Payload")
    void googleCallback_CodeExchangeFailure_ShouldReturnOkWithErrorPayload() {
        String testCode = "invalid-google-auth-code";
        String restClientErrorMessage = "Failed to connect to Google";
        
        when(restTemplate.postForEntity(
            eq(googleTokenEndpoint),
            any(HttpEntity.class),
            eq(GoogleTokenResponse.class)))
            .thenThrow(new RestClientException(restClientErrorMessage));
        
        given()
            .queryParam("code", testCode)
            .config(RestAssured.config().redirect(RedirectConfig.redirectConfig()
                .followRedirects(true)))
            .log().all()
            .when()
            .get(GOOGLE_CALLBACK_PATH)
            .then()
            .log().all()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .body("status", equalTo(HttpStatus.UNAUTHORIZED.value()))
            .body("message", containsString(
                "Failed to exchange authorization code"))
            .body("data", equalTo(false))
            .body("path", equalTo(GOOGLE_CALLBACK_PATH))
            .body("timestamp", notNullValue());
        
        verify(restTemplate).postForEntity(eq(googleTokenEndpoint), any(HttpEntity.class),
            eq(GoogleTokenResponse.class));
    }
    
    /**
     * Tests the /logout endpoint. It should clear the access_token cookie and mark the refresh
     * token as used.
     */
    @Test
    @DisplayName("POST /logout - Success with valid token")
    void logout_shouldSucceedWithValidToken() {
        // Login to get access_token cookie
        String jsonBody = String.format("""
            {
                "email": "%s",
                "password": "%s"
            }
            """, regularUserEmail, regularUserPassword);
        
        Response loginResponse = requestSpecification()
            .body(jsonBody)
            .when()
            .post(LOGIN_PATH)
            .then()
            .statusCode(HttpStatus.OK.value())
            .cookie("access_token", notNullValue())
            .extract()
            .response();
        
        Map<String, String> cookies = loginResponse.getCookies();
        
        // Create a refresh token in the database
        TokenEntity refreshToken = TokenEntity.builder()
            .user(userRepository.findByEmail(regularUserEmail).orElseThrow())
            .type(TokenType.REFRESH)
            .used(false)
            .tokenHash(UUID.randomUUID().toString())
            .expiresAt(new java.sql.Timestamp(System.currentTimeMillis() + 604800 * 1000))
            .build();
        tokenRepository.save(refreshToken);
        
        // Call logout
        requestSpecification()
            .cookies(cookies)
            .when()
            .post(LOGOUT_PATH)
            .then()
            .log().ifValidationFails()
            .statusCode(HttpStatus.OK.value())
            .body("status", equalTo(200))
            .body("message", equalTo("Logout successful"))
            .body("data", equalTo(true))
            .body("timestamp", notNullValue())
            .body("path", equalTo(LOGOUT_PATH))
            .cookie("access_token", is("")); // Cookie should be cleared
        
        TokenEntity updatedToken = tokenRepository.findById(refreshToken.getId()).orElse(null);
        assertTrue(updatedToken != null && updatedToken.isUsed(),
            "Refresh token should be marked as used");
    }
}