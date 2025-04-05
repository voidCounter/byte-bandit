package com.bytebandit.userservice.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.bytebandit.userservice.model.TokenEntity;
import com.bytebandit.userservice.model.UserEntity;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import lib.user.enums.TokenType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserAndTokenEntityIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    /**
     * Test to verify that when a user is deleted, all associated tokens are also deleted.
     */
    @Test
    void whenUserIsDeleted_thenAssociatedTokensAreDeleted() {
        UserEntity user = createUser();
        user = entityManager.persistAndFlush(user);

        TokenEntity tokenEntity = createToken(user);
        user.getTokens().add(tokenEntity);

        entityManager.persistAndFlush(tokenEntity);
        UUID tokenId = tokenEntity.getId();

        entityManager.remove(user);
        entityManager.flush();

        TokenEntity deletedToken = entityManager.find(TokenEntity.class, tokenId);
        assertNull(deletedToken);
    }

    /**
     * Test to verify that when multiple tokens are added to a user, all tokens are persisted. For
     * example, if a user has both an email verification token and a password reset token, they
     * should both be stored in the database.
     */
    @Test
    void whenMultipleTokensAreAddedToUser_thenAllTokensArePersisted() {
        UserEntity user = createUser();
        user = entityManager.persistAndFlush(user);

        TokenEntity token1 = createToken(user);
        TokenEntity token2 = createToken(user, TokenType.PASSWORD_RESET);

        user.getTokens().add(token1);
        user.getTokens().add(token2);

        entityManager.persistAndFlush(user);

        UserEntity retrievedUser = entityManager.find(UserEntity.class, user.getId());
        assertEquals(2, retrievedUser.getTokens().size());
    }

    private TokenEntity createToken(UserEntity user, TokenType type) {
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setTokenHash(UUID.randomUUID().toString());
        tokenEntity.setType(type);
        tokenEntity.setExpiresAt(Timestamp.valueOf(LocalDateTime.now().plusDays(1)));
        tokenEntity.setUser(user);
        tokenEntity.setUsed(false);
        return tokenEntity;
    }

    private TokenEntity createToken(UserEntity user) {
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setTokenHash(UUID.randomUUID().toString());
        tokenEntity.setType(TokenType.EMAIL_VERIFICATION);
        tokenEntity.setExpiresAt(Timestamp.valueOf(LocalDateTime.now().plusDays(1)));
        tokenEntity.setUser(user);
        tokenEntity.setUsed(false);
        return tokenEntity;
    }

    private UserEntity createUser() {
        UserEntity user = new UserEntity();
        user.setEmail("user@example.com");
        user.setFullName("Test User");
        user.setPasswordHash(UUID.randomUUID().toString());
        return user;
    }
}
