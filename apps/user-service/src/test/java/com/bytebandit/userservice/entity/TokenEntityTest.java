package com.bytebandit.userservice.entity;

import com.bytebandit.userservice.enums.TokenType;
import com.bytebandit.userservice.model.TokenEntity;
import com.bytebandit.userservice.model.UserEntity;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@Transactional
@Rollback
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TokenEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void whenTokenEntityIsPersisted_thenItIsSavedInDatabase() {
        UserEntity user = createAndPersistUser();

        TokenEntity token = new TokenEntity();
        token.setToken(UUID.randomUUID());
        token.setType(TokenType.EMAIL_VERIFICATION);
        token.setExpiresAt(Timestamp.valueOf(LocalDateTime.now().plusDays(1)));
        token.setUser(user);
        token.setUsed(false);

        TokenEntity savedToken = entityManager.persistFlushFind(token);

        assertNotNull(savedToken.getId());
        assertEquals(token.getToken(), savedToken.getToken());
        assertEquals(user.getId(), savedToken.getUser().getId());
        assertNotNull(savedToken.getCreatedAt());
        assertFalse(savedToken.isUsed());
    }

    @Test
    void whenMultipleTokensForSameUser_thenAllAreStored() {
        UserEntity user = createAndPersistUser();

        TokenEntity token1 = new TokenEntity();
        token1.setToken(UUID.randomUUID());
        token1.setType(TokenType.EMAIL_VERIFICATION);
        token1.setExpiresAt(Timestamp.valueOf(LocalDateTime.now().plusDays(1)));
        token1.setUser(user);
        token1.setUsed(false);

        TokenEntity token2 = new TokenEntity();
        token2.setToken(UUID.randomUUID());
        token2.setType(TokenType.PASSWORD_RESET);
        token2.setExpiresAt(Timestamp.valueOf(LocalDateTime.now().plusDays(2)));
        token2.setUser(user);
        token2.setUsed(false);

        entityManager.persist(token1);
        entityManager.persist(token2);
        entityManager.flush();

        List<TokenEntity> tokens = entityManager.getEntityManager()
                .createQuery("SELECT t FROM TokenEntity t WHERE t.user.id = :userId", TokenEntity.class)
                .setParameter("userId", user.getId())
                .getResultList();

        assertEquals(2, tokens.size());
    }

    @Test
    void whenTokenIsUpdated_thenTokenFieldDoesNotChange() {
        UserEntity user = createAndPersistUser();

        TokenEntity token = new TokenEntity();
        UUID originalToken = UUID.randomUUID();
        token.setToken(originalToken);
        token.setType(TokenType.PASSWORD_RESET);
        token.setExpiresAt(Timestamp.valueOf(LocalDateTime.now().plusDays(1)));
        token.setUser(user);
        token.setUsed(false);

        entityManager.persistAndFlush(token);

        TokenEntity fetchedToken = entityManager.find(TokenEntity.class, token.getId());
        fetchedToken.setUsed(true);
        entityManager.persistAndFlush(fetchedToken);

        TokenEntity updatedToken = entityManager.find(TokenEntity.class, token.getId());

        assertEquals(originalToken, updatedToken.getToken(), "Token field should not be updatable");
        assertTrue(updatedToken.isUsed(), "isUsed field should be updated");
    }

    @Test
    void whenTokenIsPersisted_thenCreatedAtAndExpiresAtAreSetCorrectly() {
        UserEntity user = createAndPersistUser();

        TokenEntity token = new TokenEntity();
        token.setToken(UUID.randomUUID());
        token.setType(TokenType.EMAIL_VERIFICATION);
        token.setExpiresAt(Timestamp.valueOf(LocalDateTime.now().plusDays(1)));
        token.setUser(user);
        token.setUsed(false);

        entityManager.persistAndFlush(token);

        TokenEntity persistedToken = entityManager.find(TokenEntity.class, token.getId());

        assertNotNull(persistedToken.getCreatedAt(), "createdAt should be automatically populated");
        assertNotNull(persistedToken.getExpiresAt(), "expiresAt should be set explicitly");
        assertTrue(persistedToken.getExpiresAt().after(persistedToken.getCreatedAt()), "expiresAt should be after createdAt");
    }

    @Test
    void whenPersistingTokenWithNullToken_thenConstraintViolationOccurs() {
        UserEntity user = createAndPersistUser();

        TokenEntity token = new TokenEntity();
        token.setType(TokenType.PASSWORD_RESET);
        token.setExpiresAt(Timestamp.valueOf(LocalDateTime.now().plusDays(1)));
        token.setUser(user);
        token.setUsed(false);

        assertThrows(PersistenceException.class, () -> {
            entityManager.persistAndFlush(token);
        });
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
