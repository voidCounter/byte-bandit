package com.bytebandit.userservice.service;

import com.bytebandit.userservice.dto.UserRegistrationRequest;
import com.bytebandit.userservice.dto.UserRegistrationResponse;
import com.bytebandit.userservice.enums.TokenType;
import com.bytebandit.userservice.exception.UserAlreadyExistsException;
import com.bytebandit.userservice.projection.CreateUserAndTokenProjection;
import com.bytebandit.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionTemplate transactionTemplate;
    private final RegistrationEmailService registrationEmailService;

    public UserRegistrationResponse register(
            UserRegistrationRequest registrationRequest
    ) {
        String passwordHash = passwordEncoder.encode(registrationRequest.getPassword());
        UUID token = UUID.randomUUID();
        String tokenHash = passwordEncoder.encode(token.toString());
        Timestamp tokenExpiresAt = Timestamp.from(Instant.now().plus(24, ChronoUnit.HOURS));

        try {
            CreateUserAndTokenProjection userAndToken = transactionTemplate.execute(
                    result ->  userRepository.createUserAndToken(
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
}
