package com.bytebandit.userservice.service;

import com.bytebandit.userservice.dto.UserRegistrationRequest;
import com.bytebandit.userservice.dto.UserRegistrationResponse;
import com.bytebandit.userservice.exception.UserAlreadyExistsException;
import com.bytebandit.userservice.model.TokenEntity;
import com.bytebandit.userservice.model.UserEntity;
import com.bytebandit.userservice.projection.CreateUserAndTokenProjection;
import com.bytebandit.userservice.repository.TokenRepository;
import com.bytebandit.userservice.repository.UserRepository;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import lib.user.enums.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionTemplate transactionTemplate;
    private final RegistrationEmailService registrationEmailService;

    /**
     * Registers a new user by creating user and token entries, sending a verification email, and
     * returning a response containing user details.
     *
     * @param registrationRequest the user registration request including email, password, and full
     *                            name.
     *
     * @return a UserRegistrationResponse containing user details such as ID, full name, email,
     *     verification status, and creation timestamp.
     * @throws UserAlreadyExistsException if a user with the provided email already exists.
     * @throws IllegalStateException      if the user registration process fails.
     */
    public UserRegistrationResponse register(
        UserRegistrationRequest registrationRequest
    ) {
        String passwordHash = passwordEncoder.encode(registrationRequest.getPassword());
        UUID token = UUID.randomUUID();
        String tokenHash = passwordEncoder.encode(token.toString());
        Timestamp tokenExpiresAt = Timestamp.from(Instant.now().plus(24, ChronoUnit.HOURS));

        try {
            CreateUserAndTokenProjection userAndToken = transactionTemplate.execute(
                result -> userRepository.createUserAndToken(
                    registrationRequest.getEmail(),
                    passwordHash,
                    registrationRequest.getFullName(),
                    tokenHash,
                    TokenType.EMAIL_VERIFICATION.name(),
                    tokenExpiresAt
                )
            );
            if (userAndToken == null) {
                throw new IllegalStateException("User registration failed.");
            }
            sendEmail(
                userAndToken,
                token.toString()
            );
            return new UserRegistrationResponse(
                userAndToken.getId(),
                userAndToken.getFullName(),
                userAndToken.getEmail(),
                userAndToken.getVerified(),
                userAndToken.getCreatedAt()
            );
        } catch (DataIntegrityViolationException e) {
            throw new UserAlreadyExistsException("User with provided email already exists.", e);
        }
    }

    private void sendEmail(
        CreateUserAndTokenProjection user,
        String token
    ) {
        registrationEmailService.sendEmail(
            user.getEmail(),
            user.getFullName(),
            token,
            user.getId()
        );
    }

    /**
     * Resends the verification email to the user with the provided email address.
     *
     * @param email User's requested receiver email
     */
    public void resendVerificationEmail(
        String email
    ) {
        UserEntity user = userRepository.findByEmail(
            email).orElseThrow(() -> new IllegalArgumentException(
            "If this email exists, an email will be sent.")); // avoid leaking information
        if (user.isVerified()) {
            return;
        }
        tokenRepository.invalidateAllForUserAndType(
            user, TokenType.EMAIL_VERIFICATION
        );

        UUID token = UUID.randomUUID();
        String tokenHash = passwordEncoder.encode(token.toString());
        Timestamp tokenExpiresAt = Timestamp.from(Instant.now().plus(24, ChronoUnit.HOURS));

        TokenEntity tokenEntity = TokenEntity.builder()
            .type(TokenType.EMAIL_VERIFICATION)
            .tokenHash(tokenHash)
            .used(false)
            .user(user)
            .expiresAt(tokenExpiresAt).build();
        tokenRepository.save(tokenEntity);

        registrationEmailService.sendEmail(user.getEmail(), user.getFullName(), token.toString(),
            user.getId());
    }
}
