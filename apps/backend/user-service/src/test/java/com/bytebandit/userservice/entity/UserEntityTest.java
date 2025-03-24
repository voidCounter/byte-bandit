package com.bytebandit.userservice.entity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.bytebandit.userservice.model.UserEntity;
import jakarta.transaction.Transactional;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

@DataJpaTest
@Transactional
@Rollback
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(BCryptPasswordEncoder.class)
class UserEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /** Test that the email is unique. */
    @Test
    void whenEmailIsDuplicate_thenConstraintViolation() {
        UserEntity user1 = new UserEntity();
        user1.setEmail("duplicate@example.com");
        user1.setFullName("John Doe");
        user1.setPasswordHash(passwordEncoder.encode("ValidPass1@"));
        entityManager.persist(user1);
        entityManager.flush();
        UserEntity user2 = new UserEntity();
        user2.setEmail("duplicate@example.com");
        user2.setFullName("John Doe");
        user2.setPasswordHash(passwordEncoder.encode("ValidPass1@"));
        assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persist(user2);
            entityManager.flush();
        });
    }

    /** Test that the password is valid. */
    @Test
    void whenUserIsCreated_thenVerifiedIsFalse() {
        UserEntity user = new UserEntity();
        user.setEmail("user3@example.com");
        user.setPasswordHash(passwordEncoder.encode("ValidPass1@"));
        user.setFullName("Test User");
        entityManager.persist(user);
        entityManager.flush();
        assertFalse(user.isEnabled());
    }

    /** Test that the verified status is updated correctly. */
    @Test
    void whenUserIsVerified_thenVerifiedIsUpdatesCorrectly() {
        UserEntity user = new UserEntity();
        user.setEmail("user4@example.com");
        user.setPasswordHash(passwordEncoder.encode("ValidPass1@"));
        user.setFullName("Test User");
        entityManager.persist(user);
        entityManager.flush();
        user.setVerified(true);
        entityManager.persist(user);
        entityManager.flush();
        UserEntity foundUser = entityManager.find(UserEntity.class, user.getId());
        assertTrue(foundUser.isEnabled());
    }

    /** Test that the password is hashed. */
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
