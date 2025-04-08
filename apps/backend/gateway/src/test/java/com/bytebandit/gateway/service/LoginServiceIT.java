package com.bytebandit.gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bytebandit.gateway.dto.LoginRequest;
import com.bytebandit.gateway.enums.CookieKey;
import com.bytebandit.gateway.model.UserEntity;
import com.bytebandit.gateway.repository.TokenRepository;
import com.bytebandit.gateway.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import lib.core.dto.response.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class LoginServiceIT extends AbstractPostgresContainer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockHttpServletResponse mockHttpServletResponse;

    private final String userEmail = "test-user@mail.com";
    private final String password = "ValidPass#123";
    @Autowired
    private TokenRepository tokenRepository;

    /**
     * This method sets up the test environment by creating a mock HTTP servlet response and
     * saving a user with a specified email and password hash in the user repository.
     */
    @BeforeEach
    void setup() {
        UserEntity user = new UserEntity();
        user.setEmail(userEmail);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setVerified(true);
        userRepository.save(user);

        mockHttpServletResponse = new MockHttpServletResponse();
    }

    /**
     * Checks to see if login functionality is working correctly.
     */
    @Test
    void testLogin_Success() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(userEmail);
        loginRequest.setPassword(password);

        ApiResponse<Boolean> result = userLoginService.login(loginRequest, mockHttpServletResponse);

        // Then
        assertEquals(200, result.getStatus());
        assertTrue(result.getData());
        assertEquals("Login successful", result.getMessage());
        assertEquals("/api/v1/auth/login", result.getPath());

        // Check if access token cookie is set
        Cookie tokenCookie = mockHttpServletResponse.getCookie(CookieKey.ACCESS_TOKEN.getKey());
        assertNotNull(tokenCookie);
        assertFalse(tokenCookie.getValue().isEmpty());
        assertEquals("/", tokenCookie.getPath());
    }
}
