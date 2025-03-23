package com.bytebandit.userservice.service;

import com.bytebandit.userservice.enums.TokenType;
import com.bytebandit.userservice.exception.FailedEmailVerificationAttemptException;
import com.bytebandit.userservice.model.TokenEntity;
import com.bytebandit.userservice.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class TokenVerificationService {

    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;


    public boolean verifyToken(
            String token,
            String userId,
            TokenType tokenType
    ) {

        TokenEntity tokenEntity = tokenRepository.findByUserIdAndTypeAndExpiresAtAfterAndUsedIsFalse(
                UUID.fromString(userId),
                tokenType,
                Timestamp.from(Instant.now())
        ).orElseThrow(
                () -> new FailedEmailVerificationAttemptException("Token is corrupted.")
        );

        if (!passwordEncoder.matches(token, tokenEntity.getTokenHash()) ||
                tokenEntity.getExpiresAt().before(Timestamp.from(Instant.now()))
        ) {
            throw new FailedEmailVerificationAttemptException("Token is invalid");
        }

        log.info("Verifying email for token {}", tokenEntity);
        tokenEntity.setUsed(true);
        tokenEntity = tokenRepository.save(tokenEntity);
        log.info("Verified email for token {}", tokenEntity);
        return true;
    }
}
