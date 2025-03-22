package com.bytebandit.userservice.entity;

import com.bytebandit.userservice.enums.TokenType;
import com.bytebandit.userservice.model.TokenEntity;
import com.bytebandit.userservice.model.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(BCryptPasswordEncoder.class)
class TokenEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void whenTokenEntityIsPersisted_thenTokenIsHashedAndIsSavedInDatabase() {
        UserEntity user = createAndPersistUser();
        TokenEntity tokenEntity = TokenEntity.builder()
                .tokenHash(
                        passwordEncoder.encode(UUID.randomUUID().toString())
                )
                .type(TokenType.EMAIL_VERIFICATION)
                .expiresAt(Timestamp.valueOf(LocalDateTime.now().plusDays(1)))
                .user(user)
                .isUsed(false)
                .build();

        TokenEntity savedToken = entityManager.persistFlushFind(tokenEntity);

        assertNotNull(savedToken.getId());
        assertEquals(tokenEntity.getTokenHash(), savedToken.getTokenHash());
        assertEquals(user.getId(), savedToken.getUser().getId());
        assertNotNull(savedToken.getCreatedAt());
        assertFalse(savedToken.isUsed());
    }

    @Test
    void whenTokenIsExpired_thenIsExpiredReturnsTrue() {
        UserEntity user = createAndPersistUser();
        TokenEntity tokenEntity = TokenEntity.builder()
                .tokenHash(passwordEncoder.encode(UUID.randomUUID().toString()))
                .type(TokenType.EMAIL_VERIFICATION)
                .expiresAt(Timestamp.valueOf(LocalDateTime.now().minusDays(1))) // Past date
                .user(user)
                .isUsed(false)
                .build();

        entityManager.persistFlushFind(tokenEntity);

        assertTrue(tokenEntity.getExpiresAt().before(new Timestamp(System.currentTimeMillis())));
    }

    @Test
    void whenDifferentTokenTypesArePersisted_thenTheyAreSavedCorrectly() {
        UserEntity user = createAndPersistUser();

        TokenEntity emailVerificationToken = createToken(user, TokenType.EMAIL_VERIFICATION);
        TokenEntity passwordResetToken = createToken(user, TokenType.PASSWORD_RESET);

        TokenEntity savedEmailToken = entityManager.persistFlushFind(emailVerificationToken);
        TokenEntity savedPasswordToken = entityManager.persistFlushFind(passwordResetToken);

        assertEquals(TokenType.EMAIL_VERIFICATION, savedEmailToken.getType());
        assertEquals(TokenType.PASSWORD_RESET, savedPasswordToken.getType());
    }

    private TokenEntity createToken(UserEntity user, TokenType type) {
        return TokenEntity.builder()
                .tokenHash(passwordEncoder.encode(UUID.randomUUID().toString()))
                .type(type)
                .expiresAt(Timestamp.valueOf(LocalDateTime.now().plusDays(1)))
                .user(user)
                .isUsed(false)
                .build();
    }

    private UserEntity createAndPersistUser() {
        UserEntity user = new UserEntity();
        user.setEmail("user@example.com");
        user.setFullName("Test User");
        user.setPasswordHash("hashedPassword");
        entityManager.persistAndFlush(user);
        return user;
    }
}
