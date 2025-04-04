package com.bytebandit.userservice.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.bytebandit.userservice.enums.TokenType;
import com.bytebandit.userservice.model.TokenEntity;
import com.bytebandit.userservice.model.UserEntity;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@Import(BCryptPasswordEncoder.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserAndTokenEntityIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Test to verify that when a user is deleted, all associated tokens are also deleted.
     */
    @Test
    void whenUserIsDeleted_thenAssociatedTokensAreDeleted() {
        UserEntity user = createAndPersistUser();

        TokenEntity tokenEntity = TokenEntity.builder()
            .tokenHash(passwordEncoder.encode(UUID.randomUUID().toString()))
            .type(TokenType.REFRESH)
            .expiresAt(Timestamp.valueOf(LocalDateTime.now().plusDays(1)))
            .user(user)
            .used(false)
            .build();
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
        UserEntity user = createAndPersistUser();

        TokenEntity token1 = TokenEntity.builder()
            .tokenHash(passwordEncoder.encode(UUID.randomUUID().toString()))
            .type(TokenType.EMAIL_VERIFICATION)
            .expiresAt(Timestamp.valueOf(LocalDateTime.now().plusDays(1)))
            .user(user)
            .used(false)
            .build();

        TokenEntity token2 = TokenEntity.builder()
            .tokenHash(passwordEncoder.encode(UUID.randomUUID().toString()))
            .type(TokenType.PASSWORD_RESET)
            .expiresAt(Timestamp.valueOf(LocalDateTime.now().plusDays(2)))
            .user(user)
            .used(false)
            .build();

        user.getTokens().add(token1);
        user.getTokens().add(token2);

        entityManager.persistAndFlush(user);

        UserEntity retrievedUser = entityManager.find(UserEntity.class, user.getId());
        assertEquals(2, retrievedUser.getTokens().size());
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
