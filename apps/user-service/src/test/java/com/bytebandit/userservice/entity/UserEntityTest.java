package com.bytebandit.userservice.entity;

import com.bytebandit.userservice.model.UserEntity;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserEntityTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenEmailIsInvalid_thenEmailViolation() {
        UserEntity user = new UserEntity();
        user.setEmail("invalid-email");
        user.setPassword("ValidPass1@");
        user.setUsername("validUser");
        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Invalid email format detected", violations.iterator().next().getMessage());
    }

    @Test
    void whenEmailIsNull_thenNotNullViolation() {
        UserEntity user = new UserEntity();
        user.setEmail(null); // Null value
        user.setPassword("ValidPass1@");
        user.setUsername("validUser");
        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Email field cannot be null", violations.iterator().next().getMessage());
    }

    @Test
    void whenPasswordIsWeak_thenPasswordViolation() {
        UserEntity user = new UserEntity();
        user.setEmail("valid@example.com");
        user.setPassword("weak");
        user.setUsername("validUser");
        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Password must be at least 8 characters"));
    }

    @Test
    void whenPasswordIsNull_thenNotNullViolation() {
        UserEntity user = new UserEntity();
        user.setEmail("valid@example.com");
        user.setPassword(null);
        user.setUsername("validUser");
        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Password field cannot be null", violations.iterator().next().getMessage());
    }

    @Test
    void whenUsernameTooShort_thenSizeViolation() {
        UserEntity user = new UserEntity();
        user.setEmail("valid@example.com");
        user.setPassword("ValidPass1@");
        user.setUsername("abc");
        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Username must be between 4 and 15 characters", violations.iterator().next().getMessage());
    }

    @Test
    void whenUsernameIsNull_thenNotNullViolation() {
        UserEntity user = new UserEntity();
        user.setEmail("valid@example.com");
        user.setPassword("ValidPass1@");
        user.setUsername(null);
        Set<ConstraintViolation<UserEntity>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        assertEquals("Username field cannot be null", violations.iterator().next().getMessage());
    }
}
