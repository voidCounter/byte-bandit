package com.bytebandit.userservice.service;

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
import java.util.UUID;
import lib.user.enums.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class TokenVerificationService {

    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    /**
     * Verifies the provided token against the stored token hash for the given user ID and token.
     *
     * @param token     The token to verify
     * @param userId    The user ID associated with the token
     * @param tokenType The type of token (e.g., EMAIL_VERIFICATION)
     */
    public void verifyToken(String token, String userId, TokenType tokenType) {

        TokenEntity tokenEntity =
            tokenRepository.findByUserIdAndTypeAndCreatedAtBeforeAndExpiresAtAfterAndUsedIsFalse(
                UUID.fromString(userId), tokenType, Timestamp.from(Instant.now()),
                Timestamp.from(Instant.now())).orElseThrow(() -> {
                    if (tokenType == TokenType.EMAIL_VERIFICATION) {
                        return new EmailVerificationExpiredException(
                        "Email verification token is expired.");
                    }
                    return new TokenExpiredException("Token is expired.");
                });

        if (!passwordEncoder.matches(token, tokenEntity.getTokenHash())) {

            if (tokenType == TokenType.EMAIL_VERIFICATION) {
                throw new InvalidEmailVerificationLinkException("Email verification link is "
                                                                    + "invalid.");
            }
            throw new InvalidTokenException("Token is invalid");
        }

        tokenEntity.setUsed(true);
        tokenRepository.save(tokenEntity);
        UserEntity user = tokenEntity.getUser();
        user.setVerified(true);
        userRepository.save(user);
    }
}
