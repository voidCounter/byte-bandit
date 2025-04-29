package com.bytebandit.gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bytebandit.gateway.exception.InvalidTokenException;
import com.bytebandit.gateway.model.TokenEntity;
import com.bytebandit.gateway.model.UserEntity;
import com.bytebandit.gateway.repository.TokenRepository;
import com.bytebandit.gateway.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lib.user.enums.TokenType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.shaded.org.yaml.snakeyaml.tokens.Token;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
    
    @Mock
    private TokenRepository tokenRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private TokenService tokenService;
    
    private UserDetails userDetails;
    private UUID userId;
    private String secretKey;
    private final long accessTokenExpiration = 900;
    private final long refreshTokenExpiration = 604800;
    
    /**
     * This method sets up the test environment by initializing the user details and secret key.
     */
    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userDetails = new User("test@example.com", "password", Collections.emptyList());
        
        secretKey = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=";
        ReflectionTestUtils.setField(tokenService, "secretKey", secretKey);
    }
    
    /**
     * This method tests the generation of a JWT token with the correct claims.
     */
    @Test
    void generateToken_ShouldGenerateValidJwt_WithCorrectClaims() {
        String token = tokenService.generateToken(userDetails, accessTokenExpiration, userId);
        
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(Base64.getDecoder().decode(secretKey))
            .build()
            .parseClaimsJws(token)
            .getBody();
        
        assertEquals(userDetails.getUsername(), claims.getSubject());
        assertEquals(userId.toString(), claims.get("userid"));
        assertInstanceOf(List.class, claims.get("authorities"));
        assertTrue(((List<?>) claims.get("authorities")).isEmpty());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(new Date()));
    }
    
    /**
     * This method tests saving a refresh token and invalidating the old one.
     */
    @Test
    void generateAndSaveRefreshToken_WithAccessToken_ShouldSaveNewToken_AndInvalidateOld() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        TokenEntity existingToken = TokenEntity.builder()
            .id(UUID.randomUUID())
            .user(userEntity)
            .type(TokenType.REFRESH)
            .used(false)
            .tokenHash(UUID.randomUUID().toString())
            .expiresAt(new Timestamp(System.currentTimeMillis() + refreshTokenExpiration * 1000))
            .build();
        
        when(tokenRepository.findAllByUserIdAndTypeAndUsed(userId, TokenType.REFRESH, false))
            .thenReturn(Collections.singletonList(existingToken));
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(tokenRepository.save(any(TokenEntity.class))).thenAnswer(invocation -> {
            TokenEntity token = invocation.getArgument(0);
            token.setId(UUID.randomUUID());
            return token;
        });
        
        String accessToken = tokenService.generateToken(userDetails, accessTokenExpiration, userId);
        
        tokenService.generateAndSaveRefreshToken(userDetails, refreshTokenExpiration, accessToken);
        
        verify(tokenRepository, times(2)).findAllByUserIdAndTypeAndUsed(
            userId, TokenType.REFRESH,
            false);
        verify(tokenRepository).saveAll(argThat(iterable -> {
            if (!(iterable instanceof List<TokenEntity> tokens)) {
                return false;
            }
            return tokens.size() == 1
                &&
                tokens.get(0).isUsed()
                &&
                tokens.get(0).getExpiresAt().before(new Date());
        }));
        verify(tokenRepository).save(argThat(token ->
            token.getType() == TokenType.REFRESH
                &&
                !token.isUsed()
                &&
                token.getUser().getId().equals(userId)
                &&
                token.getExpiresAt().after(new Date())
        ));
    }
    
    /**
     * This method tests the generation of a refresh token with an access token that has no valid
     * refresh token.
     */
    @Test
    void generateAndSaveRefreshToken_WithAccessToken_ShouldThrowException_WhenNoValidToken() {
        String accessToken = tokenService.generateToken(userDetails, accessTokenExpiration, userId);
        when(tokenRepository.findAllByUserIdAndTypeAndUsed(userId, TokenType.REFRESH, false))
            .thenReturn(Collections.emptyList());
        
        assertThrows(InvalidTokenException.class, () ->
            tokenService.generateAndSaveRefreshToken(userDetails, refreshTokenExpiration,
                accessToken));
    }
    
    /**
     * This method tests the generation of a refresh token with a user ID.
     */
    @Test
    void generateAndSaveRefreshToken_WithUserId_ShouldSaveNewToken_AndInvalidateOld() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        TokenEntity existingToken = TokenEntity.builder()
            .id(UUID.randomUUID())
            .user(userEntity)
            .type(TokenType.REFRESH)
            .used(false)
            .tokenHash(UUID.randomUUID().toString())
            .expiresAt(new Timestamp(System.currentTimeMillis() + refreshTokenExpiration * 1000))
            .build();
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(tokenRepository.findAllByUserIdAndTypeAndUsed(userId, TokenType.REFRESH, false))
            .thenReturn(Collections.singletonList(existingToken));
        when(tokenRepository.save(any(TokenEntity.class))).thenAnswer(invocation -> {
            TokenEntity token = invocation.getArgument(0);
            token.setId(UUID.randomUUID());
            return token;
        });
        
        // act
        tokenService.generateAndSaveRefreshToken(userDetails, refreshTokenExpiration, userId);
        
        // assert
        verify(tokenRepository).findAllByUserIdAndTypeAndUsed(userId, TokenType.REFRESH, false);
        verify(tokenRepository).saveAll(argThat(tokens -> {
            if (!(tokens instanceof List<TokenEntity> tokenList)) {
                return false;
            }
            return tokens.spliterator().getExactSizeIfKnown() == 1 && tokenList.get(0).isUsed()
                &&
                tokenList.get(0).getExpiresAt().before(new Date());
        }));
        verify(tokenRepository).save(argThat(token ->
            token.getType() == TokenType.REFRESH
                &&
                !token.isUsed()
                &&
                token.getUser().getId().equals(userId)
                &&
                token.getExpiresAt().after(new Date())
        ));
    }
    
    /**
     * This method tests the generation of a refresh token with a user ID when the user is not
     * found.
     */
    @Test
    void generateAndSaveRefreshToken_WithUserId_ShouldThrowException_WhenUserNotFound() {
        // arrange
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // act & assert
        assertThrows(InvalidTokenException.class, () ->
            tokenService.generateAndSaveRefreshToken(userDetails, refreshTokenExpiration, userId));
    }
    
    /**
     * This method tests the invalidation of all refresh tokens for a user ID. It checks if all
     * tokens are marked as used and their expiration time is set to the current time.
     */
    @Test
    void invalidateAllRefreshToken_ShouldMarkAllTokensUsed_AndSetExpired() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);
        TokenEntity token1 = TokenEntity.builder()
            .id(UUID.randomUUID())
            .user(userEntity)
            .type(TokenType.REFRESH)
            .used(false)
            .tokenHash(UUID.randomUUID().toString())
            .expiresAt(new Timestamp(System.currentTimeMillis() + refreshTokenExpiration * 1000))
            .build();
        TokenEntity token2 = TokenEntity.builder()
            .id(UUID.randomUUID())
            .user(userEntity)
            .type(TokenType.REFRESH)
            .used(false)
            .tokenHash(UUID.randomUUID().toString())
            .expiresAt(new Timestamp(System.currentTimeMillis() + refreshTokenExpiration * 1000))
            .build();
        
        when(tokenRepository.findAllByUserIdAndTypeAndUsed(userId, TokenType.REFRESH, false))
            .thenReturn(Arrays.asList(token1, token2));
        
        // Act
        tokenService.invalidateAllRefreshToken(userId);
        
        // Assert
        verify(tokenRepository).saveAll(argThat(tokens -> {
            if (!(tokens instanceof List<TokenEntity> tokenList)) {
                return false;
            }
            return tokens.spliterator().getExactSizeIfKnown() == 2
                &&
                tokenList.stream()
                    .allMatch(token -> token.isUsed() && token.getExpiresAt().before(new Date()));
        }));
    }
    
    /**
     * This method tests the invalidation of all refresh tokens for a user ID when no tokens are
     * found.
     */
    @Test
    void isValidToken_ShouldReturnTrue_ForValidToken() {
        String token = tokenService.generateToken(userDetails, accessTokenExpiration, userId);
        
        // act
        boolean isValid = tokenService.isValidToken(token, userDetails);
        
        // assert
        assertTrue(isValid);
    }
    
    /**
     * This method tests if isValidToken returns false for an invalid token. It checks if the token
     * is not valid and the exception is thrown.
     */
    @Test
    void isValidToken_ShouldReturnFalse_ForExpiredToken() {
        String token = tokenService.generateToken(userDetails, -1, userId);
        
        boolean isValid = tokenService.isValidToken(token, userDetails);
        
        assertFalse(isValid);
    }
    
    /**
     * This method tests if isValidToken returns false for a token with a wrong username. It checks
     * if the token is not valid and the exception is thrown.
     */
    @Test
    void isValidToken_ShouldReturnFalse_ForWrongUsername() {
        String token = tokenService.generateToken(userDetails, accessTokenExpiration, userId);
        UserDetails wrongUser = new User("wrong@example.com", "password", Collections.emptyList());
        
        boolean isValid = tokenService.isValidToken(token, wrongUser);
        
        assertFalse(isValid);
    }
    
    /**
     * This method tests if extractUserId returns the correct user ID from a valid token.
     */
    @Test
    void extractUserId_ShouldReturnCorrectUserId() {
        String token = tokenService.generateToken(userDetails, accessTokenExpiration, userId);
        UUID extractedUserId = tokenService.extractUserId(token);
        assertEquals(userId, extractedUserId);
    }
    
    /**
     * This method tests if extractUserId throws an exception for an invalid token.
     */
    @Test
    void extractUserId_ShouldThrowException_ForInvalidToken() {
        String invalidToken = "invalid.token.here";
        assertThrows(InvalidTokenException.class, () -> tokenService.extractUserId(invalidToken));
    }
    
    /**
     * This method tests if extractUsername returns the correct username from a valid token.
     */
    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        String token = tokenService.generateToken(userDetails, accessTokenExpiration, userId);
        String username = tokenService.extractUsername(token);
        assertEquals(userDetails.getUsername(), username);
    }
    
    /**
     * This method tests if extractAllClaims returns the correct claims from a valid token.
     */
    @Test
    void extractAllClaims_ShouldReturnClaims_ForValidToken() {
        String token = tokenService.generateToken(userDetails, accessTokenExpiration, userId);
        
        Claims claims = tokenService.extractAllClaims(token);
        
        // assert
        assertEquals(userDetails.getUsername(), claims.getSubject());
        assertEquals(userId.toString(), claims.get("userid"));
        assertInstanceOf(List.class, claims.get("authorities"));
    }
    
    /**
     * This method tests if extractAllClaims returns claims for an expired token. As we're using
     * refreshing token, we can still extract claims from it.
     */
    @Test
    void extractAllClaims_ShouldReturnClaims_ForExpiredToken() {
        String token = tokenService.generateToken(userDetails, -1, userId);
        
        Claims claims = tokenService.extractAllClaims(token);
        
        assertEquals(userDetails.getUsername(), claims.getSubject());
        assertEquals(userId.toString(), claims.get("userid"));
    }
    
    /**
     * This method tests if extractAllClaims throws an exception for an invalid token.
     */
    @Test
    void extractAllClaims_ShouldThrowException_ForInvalidToken() {
        String invalidToken = "invalid.token.here";
        
        // Act & Assert
        assertThrows(InvalidTokenException.class,
            () -> tokenService.extractAllClaims(invalidToken));
    }
    
    /**
     * This method tests if isTokenExpired returns false for a valid token.
     */
    @Test
    void isTokenExpired_ShouldReturnFalse_ForValidToken() {
        String token = tokenService.generateToken(userDetails, accessTokenExpiration, userId);
        
        // Act
        boolean isExpired = tokenService.isTokenExpired(token);
        
        // Assert
        assertFalse(isExpired);
    }
    
    /**
     * This method tests if isTokenExpired returns true for an expired token.
     */
    @Test
    void isTokenExpired_ShouldReturnTrue_ForExpiredToken() {
        String token = tokenService.generateToken(userDetails, -1, userId);
        boolean isExpired = tokenService.isTokenExpired(token);
        assertTrue(isExpired);
    }
}
