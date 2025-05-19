package com.bytebandit.userservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bytebandit.userservice.exception.EmailVerificationExpiredException;
import com.bytebandit.userservice.exception.InvalidEmailVerificationLinkException;
import com.bytebandit.userservice.exception.InvalidTokenException;
import com.bytebandit.userservice.exception.TokenExpiredException;
import com.bytebandit.userservice.model.TokenEntity;
import com.bytebandit.userservice.model.UserEntity;
import com.bytebandit.userservice.repository.TokenRepository;
import com.bytebandit.userservice.repository.UserRepository;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lib.core.enums.UserAction;
import lib.core.events.UserEvent;
import lib.user.enums.TokenType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class TokenVerificationServiceTest {
    
    @Mock
    private TokenRepository tokenRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private UserEventProducer userEventProducer;
    
    @InjectMocks
    private TokenVerificationService tokenVerificationService;
    
    private String rawToken;
    private String userIdString;
    private UUID userId;
    private TokenEntity mockTokenEntity;
    private UserEntity mockUserEntity;
    
    /**
     * Sets up the necessary mock data and objects required for each test execution.
     */
    @BeforeEach
    void setUp() {
        rawToken = "test-token-123";
        userId = UUID.randomUUID();
        userIdString = userId.toString();
        
        mockUserEntity = new UserEntity();
        mockUserEntity.setId(userId);
        mockUserEntity.setEmail("test@example.com");
        mockUserEntity.setVerified(false); // start as not verified
        
        mockTokenEntity = new TokenEntity();
        mockTokenEntity.setId(UUID.randomUUID());
        mockTokenEntity.setUser(mockUserEntity);
        mockTokenEntity.setTokenHash("hashed-token");
        mockTokenEntity.setType(TokenType.EMAIL_VERIFICATION);
        mockTokenEntity.setUsed(false);
        // set timestamps
        mockTokenEntity.setCreatedAt(
            Timestamp.from(Instant.now().minusSeconds(60))); // Created recently
        mockTokenEntity.setExpiresAt(
            Timestamp.from(Instant.now().plusSeconds(3600))); // Expires in the future
    }
    
    /**
     * Tests the successful verification of an email verification token.
     */
    @Test
    void verifyToken_Success_EmailVerification() {
        when(tokenRepository.findByUserIdAndTypeAndCreatedAtBeforeAndExpiresAtAfterAndUsedIsFalse(
            eq(userId), eq(TokenType.EMAIL_VERIFICATION), any(Timestamp.class),
            any(Timestamp.class))).thenReturn(Optional.of(mockTokenEntity));
        when(passwordEncoder.matches(rawToken, mockTokenEntity.getTokenHash())).thenReturn(true);
        
        tokenVerificationService.verifyToken(rawToken, userIdString, TokenType.EMAIL_VERIFICATION);
        
        // assert
        ArgumentCaptor<TokenEntity> tokenCaptor = ArgumentCaptor.forClass(TokenEntity.class);
        verify(tokenRepository).save(tokenCaptor.capture());
        assertTrue(tokenCaptor.getValue().isUsed()); // Verify token marked as used
        
        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(userCaptor.capture());
        assertTrue(userCaptor.getValue().isVerified()); // Verify user marked as verified
        
        ArgumentCaptor<UserEvent> eventCaptor = ArgumentCaptor.forClass(UserEvent.class);
        verify(userEventProducer).sendUserEvent(eventCaptor.capture());
        assertEquals(userId, eventCaptor.getValue().getUserId());
        assertEquals(mockUserEntity.getEmail(), eventCaptor.getValue().getEmail());
        assertEquals(UserAction.USER_VERIFIED, eventCaptor.getValue().getAction());
        
        verify(
            tokenRepository).findByUserIdAndTypeAndCreatedAtBeforeAndExpiresAtAfterAndUsedIsFalse(
            any(), any(), any(), any());
        verify(passwordEncoder).matches(anyString(), anyString());
    }
    
    /**
     * Tests when EmailVerification token not found, or expired, throws
     * EmailVerificationExpiredException.
     */
    @Test
    void verifyToken_NotFoundOrExpired_EmailVerification_ThrowsEmailVerifExpiredException() {
        when(tokenRepository.findByUserIdAndTypeAndCreatedAtBeforeAndExpiresAtAfterAndUsedIsFalse(
            eq(userId), eq(TokenType.EMAIL_VERIFICATION), any(Timestamp.class),
            any(Timestamp.class))).thenReturn(Optional.empty());
        
        assertThrows(EmailVerificationExpiredException.class, () -> {
            tokenVerificationService.verifyToken(rawToken, userIdString,
                TokenType.EMAIL_VERIFICATION);
        });
        
        // Verify no further interactions
        verify(passwordEncoder, never()).matches(any(), any());
        verify(tokenRepository, never()).save(any());
        verify(userRepository, never()).save(any());
        verify(userEventProducer, never()).sendUserEvent(any());
    }
    
    /**
     * When tokens other than EmailVerification are not found or expired, it should throw
     * TokenExpiredException.
     */
    @Test
    void verifyToken_TokenNotFoundOrExpired_OtherType_ThrowsTokenExpiredException() {
        // Using a different token type
        TokenType otherType = TokenType.PASSWORD_RESET;
        when(tokenRepository.findByUserIdAndTypeAndCreatedAtBeforeAndExpiresAtAfterAndUsedIsFalse(
            eq(userId), eq(otherType), any(Timestamp.class), any(Timestamp.class))).thenReturn(
            Optional.empty());
        
        // Act and assert
        assertThrows(TokenExpiredException.class, () -> {
            tokenVerificationService.verifyToken(rawToken, userIdString, otherType);
        });
        
        // Verify no further interactions
        verify(passwordEncoder, never()).matches(any(), any());
        verify(tokenRepository, never()).save(any());
        verify(userRepository, never()).save(any());
        verify(userEventProducer, never()).sendUserEvent(any());
    }
    
    
    /**
     * Tests the scenario where the token hash does not match the provided token.
     */
    @Test
    void verifyToken_InvalidTokenHash_EmailVerif_ThrowsInvalidEmailVerifLinkException() {
        // Arrange
        when(tokenRepository.findByUserIdAndTypeAndCreatedAtBeforeAndExpiresAtAfterAndUsedIsFalse(
            eq(userId), eq(TokenType.EMAIL_VERIFICATION), any(Timestamp.class),
            any(Timestamp.class))).thenReturn(Optional.of(mockTokenEntity));
        // Simulate password mismatch
        when(passwordEncoder.matches(rawToken, mockTokenEntity.getTokenHash())).thenReturn(false);
        
        // Act and assert
        assertThrows(InvalidEmailVerificationLinkException.class, () -> {
            tokenVerificationService.verifyToken(rawToken, userIdString,
                TokenType.EMAIL_VERIFICATION);
        });
        
        // Verify no save/event sending occurred
        verify(tokenRepository, never()).save(any());
        verify(userRepository, never()).save(any());
        verify(userEventProducer, never()).sendUserEvent(any());
        verify(
            tokenRepository).findByUserIdAndTypeAndCreatedAtBeforeAndExpiresAtAfterAndUsedIsFalse(
            any(), any(), any(), any());
        verify(passwordEncoder).matches(anyString(), anyString());
    }
    
    /**
     * Tests the scenario where the token hash does not match the provided token for a different
     * token type.
     */
    @Test
    void verifyToken_InvalidTokenHash_OtherType_ThrowsInvalidTokenException() {
        // Arrange
        TokenType otherType = TokenType.PASSWORD_RESET;
        mockTokenEntity.setType(otherType); // Adjust mock entity if necessary
        when(tokenRepository.findByUserIdAndTypeAndCreatedAtBeforeAndExpiresAtAfterAndUsedIsFalse(
            eq(userId), eq(otherType), any(Timestamp.class), any(Timestamp.class))).thenReturn(
            Optional.of(mockTokenEntity));
        // Simulate password mismatch
        when(passwordEncoder.matches(rawToken, mockTokenEntity.getTokenHash())).thenReturn(false);
        
        // Act and assert
        assertThrows(InvalidTokenException.class, () -> {
            tokenVerificationService.verifyToken(rawToken, userIdString, otherType);
        });
        
        // Verify no save/event sending occurred
        verify(tokenRepository, never()).save(any());
        verify(userRepository, never()).save(any());
        verify(userEventProducer, never()).sendUserEvent(any());
        verify(
            tokenRepository).findByUserIdAndTypeAndCreatedAtBeforeAndExpiresAtAfterAndUsedIsFalse(
            any(), any(), any(), any());
        verify(passwordEncoder).matches(anyString(), anyString());
    }
}
