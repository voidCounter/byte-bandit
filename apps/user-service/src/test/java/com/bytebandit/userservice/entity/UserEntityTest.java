package com.bytebandit.userservice.entity;

import com.bytebandit.userservice.model.UserEntity;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void whenEmailIsDuplicate_thenConstraintViolation() {
        UserEntity user1 = new UserEntity();
        user1.setEmail("duplicate@example.com");
        user1.setUsername("user1");
        user1.setPassword("ValidPass1@");
        entityManager.persist(user1);
        entityManager.flush();
        UserEntity user2 = new UserEntity();
        user2.setEmail("duplicate@example.com");
        user2.setUsername("user2");
        user2.setPassword("ValidPass1@");
        assertThrows(Exception.class, () -> {
            entityManager.persist(user2);
            entityManager.flush();
        });
    }

    @Test
    void whenUsernameIsDuplicate_thenConstraintViolation() {
        UserEntity user1 = new UserEntity();
        user1.setEmail("user1@example.com");
        user1.setUsername("duplicate");
        user1.setPassword("ValidPass1@");
        entityManager.persist(user1);
        entityManager.flush();
        UserEntity user2 = new UserEntity();
        user2.setEmail("user2@example.com");
        user2.setUsername("duplicate");
        user2.setPassword("ValidPass1@");
        assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persist(user2);
            entityManager.flush();
        });
    }

    @Test
    void whenUserIsCreated_thenIsEnabledIsFalse() {
        UserEntity user = new UserEntity();
        user.setEmail("user@example.com");
        user.setPassword("ValidPass1@");
        user.setUsername("testuser");

        entityManager.persist(user);
        entityManager.flush();

        assertFalse(user.isEnabled());
    }

    @Test
    void whenUserIsEnabled_thenIsEnabledUpdatesCorrectly() {
        UserEntity user = new UserEntity();
        user.setEmail("user@example.com");
        user.setPassword("ValidPass1@");
        user.setUsername("testuser");

        entityManager.persist(user);
        entityManager.flush();

        user.setEnabled(true);
        entityManager.persist(user);
        entityManager.flush();

        UserEntity foundUser = entityManager.find(UserEntity.class, user.getId());
        assertTrue(foundUser.isEnabled());
    }
}
