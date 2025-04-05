package com.bytebandit.userservice.entity;

import static com.bytebandit.userservice.utils.TestUtils.createToken;
import static com.bytebandit.userservice.utils.TestUtils.createUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bytebandit.userservice.model.TokenEntity;
import com.bytebandit.userservice.model.UserEntity;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import lib.user.enums.TokenType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TokenEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    /**
     * Test to verify that a token entity can be persisted and that the token is hashed.
     */
    @Test
    void whenTokenEntityIsPersisted_thenTokenIsHashedAndIsSavedInDatabase() {
        UserEntity user = createUser();
        user = entityManager.persistAndFlush(user);

        TokenEntity tokenEntity = createToken(user);
        TokenEntity savedToken = entityManager.persistFlushFind(tokenEntity);

        assertNotNull(savedToken.getId());
        assertEquals(tokenEntity.getTokenHash(), savedToken.getTokenHash());
        assertEquals(user.getId(), savedToken.getUser().getId());
        assertNotNull(savedToken.getCreatedAt());
        assertFalse(savedToken.isUsed());
    }

    /**
     * Test to verify that the token entity's expiration date is set correctly.
     */
    @Test
    void whenTokenIsExpired_thenIsExpiredReturnsTrue() {
        UserEntity user = createUser();
        entityManager.persistAndFlush(user);

        TokenEntity tokenEntity = createToken(user);
        tokenEntity.setExpiresAt(Timestamp.valueOf(LocalDateTime.now().minusDays(1)));
        entityManager.persistFlushFind(tokenEntity);

        assertTrue(tokenEntity.getExpiresAt().before(new Timestamp(System.currentTimeMillis())));
    }

    /**
     * Test to verify that when different token types are persisted, they are saved correctly.
     */
    @Test
    void whenDifferentTokenTypesArePersisted_thenTheyAreSavedCorrectly() {
        UserEntity user = createUser();
        entityManager.persistAndFlush(user);

        TokenEntity emailVerificationToken = createToken(user, TokenType.EMAIL_VERIFICATION);
        TokenEntity passwordResetToken = createToken(user, TokenType.PASSWORD_RESET);

        TokenEntity savedEmailToken = entityManager.persistFlushFind(emailVerificationToken);
        TokenEntity savedPasswordToken = entityManager.persistFlushFind(passwordResetToken);

        assertEquals(TokenType.EMAIL_VERIFICATION, savedEmailToken.getType());
        assertEquals(TokenType.PASSWORD_RESET, savedPasswordToken.getType());
    }


}
