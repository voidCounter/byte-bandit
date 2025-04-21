package com.bytebandit.gateway.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.bytebandit.gateway.configurer.AbstractPostgresContainer;
import com.bytebandit.gateway.exception.InvalidTokenException;
import com.bytebandit.gateway.model.TokenEntity;
import com.bytebandit.gateway.model.UserEntity;
import com.bytebandit.gateway.repository.TokenRepository;
import com.bytebandit.gateway.repository.UserRepository;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lib.user.enums.TokenType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TokenServiceIT extends AbstractPostgresContainer {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    private UUID testUserId;
    private String validAccessToken;
    private UserEntity testUser;

    /**
     * This method sets up the test environment by deleting all existing tokens from the repository
     * and creating a new user with a random UUID. It generates a valid access token for the user
     * with a specified expiration time.
     */
    @BeforeEach
    void setup() {
        tokenRepository.deleteAll();
        String randomText = UUID.randomUUID().toString();

        testUser = new UserEntity();
        testUser.setEmail("test-user" + randomText + "@mail.com");
        testUser.setPasswordHash("dummy");
        testUser = userRepository.save(testUser);

        testUserId = testUser.getId();

        validAccessToken = tokenService.generateToken(
            testUser,
            3600L,
            testUserId
        );

        TokenEntity entity = new TokenEntity();
        entity.setUser(testUser);
        entity.setTokenHash("dummy");
        entity.setType(TokenType.REFRESH);
        entity.setExpiresAt(Timestamp.from(Instant.now().plusSeconds(600)));

        tokenRepository.save(entity);
    }

    /**
     * This test verifies that the refresh token is generated and saved correctly in the database.
     * It checks if the refresh token is present in the database and if its expiration time is
     * greater than the current time.
     */
    @Test
    void shouldUpdateRefreshTokenInDatabase() {
        tokenService.generateAndSaveRefreshToken(
            testUser,
            1800L,
            validAccessToken
        );

        Optional<TokenEntity> updated =
            tokenRepository.findByUserIdAndType(testUserId, TokenType.REFRESH);
        assertThat(updated).isPresent();
        assertThat(updated.get().getTokenHash()).isNotEqualTo("dummy");
        assertThat(updated.get().getExpiresAt()).isAfter(Timestamp.from(Instant.now()));
    }

    /**
     * This test verifies that an exception is thrown when trying to generate a refresh token with
     * an invalid access token. It checks if the exception message is as expected.
     */
    @Test
    void shouldThrowExceptionIfTokenIsInvalid() {
        String fakeToken = "invalid.token.value";
        InvalidTokenException exception = assertThrows(
            InvalidTokenException.class,
            () -> tokenService.generateAndSaveRefreshToken(testUser, 1800, fakeToken)
        );

        assertEquals("Invalid token signature", exception.getMessage());
    }

    /**
     * This test verifies that an exception is thrown when trying to generate a refresh token for a
     * user that does not exist in the database. It checks if the exception message is as expected.
     */
    @Test
    void shouldThrowExceptionIfRefreshTokenNotFoundForUser() {
        UUID otherUserId = UUID.randomUUID();
        String otherUserToken = tokenService.generateToken(testUser, 3600, otherUserId);

        InvalidTokenException exception = assertThrows(
            InvalidTokenException.class,
            () -> tokenService.generateAndSaveRefreshToken(testUser, 1800, otherUserToken)
        );

        assertEquals("Refresh token not found for user", exception.getMessage());
    }

    /**
     * This test verifies that the token is valid and can be used
     * to extract the username and user ID from it.
     * It checks if the extracted values match the expected values.
     */
    @Test
    void shouldGenerateValidateAndUpdateTokenSuccessfully() {
        String token = tokenService.generateToken(testUser, 3600, testUserId);

        boolean isValid = tokenService.isValidToken(token, testUser);
        assertThat(isValid).isTrue();

        String username = tokenService.extractUsername(token);
        UUID extractedUserId = tokenService.extractUserId(token);

        assertThat(username).isEqualTo(testUser.getUsername());
        assertThat(extractedUserId).isEqualTo(testUserId);

        tokenService.generateAndSaveRefreshToken(testUser, 1800, token);

        Optional<TokenEntity> updated = tokenRepository.findByUserIdAndType(
            testUserId,
            TokenType.REFRESH
        );
        assertThat(updated).isPresent();
        assertThat(updated.get().getTokenHash()).isNotEqualTo("dummy");
        assertThat(updated.get().getExpiresAt()).isAfter(Timestamp.from(Instant.now()));
    }

    /**
     * This test verifies that the token is expired after a specified time period.
     */
    @Test
    void shouldThrowExceptionForExpiredToken() {
        String shortLivedToken = tokenService.generateToken(testUser, -10, testUserId);

        InvalidTokenException exception = assertThrows(
            InvalidTokenException.class,
            () -> tokenService.isTokenExpired(shortLivedToken)
        );

        assertThat(exception.getMessage()).isEqualTo("Token has expired");
    }
}