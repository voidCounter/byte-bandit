package com.bytebandit.userservice.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bytebandit.userservice.exception.FailedEmailVerificationAttemptException;
import com.bytebandit.userservice.model.TokenEntity;
import com.bytebandit.userservice.model.UserEntity;
import com.bytebandit.userservice.repository.TokenRepository;
import com.bytebandit.userservice.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import lib.user.enums.TokenType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserRegistrationServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private RegistrationEmailService registrationEmailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserRegistrationService userRegistrationService;

    /**
     * Test to verify that the resend verification email functionality works as expected.
     */
    @Test
    void givenUnverifiedUser_whenResendVerificationEmail_thenSendsEmailAndSavesToken() {
        String email = "test@example.com";
        UUID userId = UUID.randomUUID();
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setVerified(false);
        user.setId(userId);
        user.setFullName("Test User");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("hashed-token");

        userRegistrationService.resendVerificationEmail(email);

        verify(tokenRepository).invalidateAllForUserAndType(user, TokenType.EMAIL_VERIFICATION);
        verify(tokenRepository).save(any(TokenEntity.class));
        verify(registrationEmailService).sendEmail(eq(email), eq("Test User"), anyString(),
            eq(userId));
    }

    /**
     * Test to verify that an exception is thrown when the user is already verified.
     */
    @Test
    void givenVerifiedUser_whenResendVerificationEmail_thenThrowsException() {
        UserEntity user = new UserEntity();
        user.setEmail("test@example.com");
        user.setVerified(true);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThrows(FailedEmailVerificationAttemptException.class,
            () -> userRegistrationService.resendVerificationEmail("test@example.com"));
    }

    /**
     * Test to verify that an exception is thrown when the user does not exist in the database.
     */
    @Test
    void givenNonexistentUser_whenResendVerificationEmail_thenThrowsGenericException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
            () -> userRegistrationService.resendVerificationEmail("notfound@example.com"));
    }
}
