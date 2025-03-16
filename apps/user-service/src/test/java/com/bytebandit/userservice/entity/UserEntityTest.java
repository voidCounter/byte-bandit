package com.bytebandit.userservice.entity;

import com.bytebandit.userservice.model.UserEntity;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(BCryptPasswordEncoder.class)
public class UserEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void whenEmailIsDuplicate_thenConstraintViolation() {
        UserEntity user1 = new UserEntity();
        user1.setEmail("duplicate@example.com");
        user1.setFullName("John Doe");
        user1.setPasswordHash(
                passwordEncoder.encode("ValidPass1@")
        );
        entityManager.persist(user1);
        entityManager.flush();
        UserEntity user2 = new UserEntity();
        user2.setEmail("duplicate@example.com");
        user2.setFullName("John Doe");
        user2.setPasswordHash(
                passwordEncoder.encode("ValidPass1@")
        );
        assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persist(user2);
            entityManager.flush();
        });
    }


    @Test
    void whenUserIsCreated_thenVerifiedIsFalse() {
        UserEntity user = new UserEntity();
        user.setEmail("user3@example.com");
        user.setPasswordHash(
                passwordEncoder.encode("ValidPass1@")
        );
        user.setFullName("Test User");
        entityManager.persist(user);
        entityManager.flush();
        assertFalse(user.isEnabled());
    }

    @Test
    void whenUserIsVerified_thenVerifiedIsUpdatesCorrectly() {
        UserEntity user = new UserEntity();
        user.setEmail("user4@example.com");
        user.setPasswordHash(
                passwordEncoder.encode("ValidPass1@")
        );
        user.setFullName("Test User");
        entityManager.persist(user);
        entityManager.flush();
        user.setVerified(true);
        entityManager.persist(user);
        entityManager.flush();
        UserEntity foundUser = entityManager.find(UserEntity.class, user.getId());
        assertTrue(foundUser.isEnabled());
    }

    @Test
    void whenUserIsSaved_thenPasswordIsHashed() {
        UserEntity user = new UserEntity();
        user.setEmail("user@example.com");
        user.setFullName("John Doe");
        String rawPassword = "ValidPass1@";
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        entityManager.persist(user);
        entityManager.flush();
        UserEntity foundUser = entityManager.find(UserEntity.class, user.getId());
        assertNotNull(foundUser.getPasswordHash());
        assertNotEquals(rawPassword, foundUser.getPasswordHash()); // Ensure the password is hashed
        assertTrue(passwordEncoder.matches(rawPassword, foundUser.getPasswordHash()));
    }
}
