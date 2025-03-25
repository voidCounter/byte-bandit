package com.bytebandit.userservice.service;

import com.bytebandit.userservice.enums.TokenType;
import com.bytebandit.userservice.exception.FailedEmailVerificationAttemptException;
import com.bytebandit.userservice.model.TokenEntity;
import com.bytebandit.userservice.repository.TokenRepository;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class TokenVerificationService {

    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public void verifyToken(
        String token,
        String userId,
        TokenType tokenType
    ) {

        TokenEntity tokenEntity =
            tokenRepository.findByUserIdAndTypeAndCreatedAtBeforeAndExpiresAtAfterAndUsedIsFalse(
                UUID.fromString(userId),
                tokenType,
                Timestamp.from(Instant.now()),
                Timestamp.from(Instant.now())
            ).orElseThrow(
                () -> new FailedEmailVerificationAttemptException("Token is corrupted.")
            );

        if (!passwordEncoder.matches(token, tokenEntity.getTokenHash())) {
            throw new FailedEmailVerificationAttemptException("Token is invalid");
        }

        tokenEntity.setUsed(true);
        tokenRepository.save(tokenEntity);
    }
}
