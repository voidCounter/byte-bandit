package com.bytebandit.userservice.utils;

import com.bytebandit.userservice.model.TokenEntity;
import com.bytebandit.userservice.model.UserEntity;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import lib.user.enums.TokenType;

public class TestUtils {

    /** Create a token entity with a user and a specific token type. */
    public static TokenEntity createToken(UserEntity user, TokenType type) {
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setTokenHash(UUID.randomUUID().toString());
        tokenEntity.setType(type);
        tokenEntity.setExpiresAt(Timestamp.valueOf(LocalDateTime.now().plusDays(1)));
        tokenEntity.setUser(user);
        tokenEntity.setUsed(false);
        return tokenEntity;
    }

    /** Create a token entity with a user and the default token type (EMAIL_VERIFICATION). */
    public static TokenEntity createToken(UserEntity user) {
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setTokenHash("hashedToken");
        tokenEntity.setType(TokenType.EMAIL_VERIFICATION);
        tokenEntity.setExpiresAt(Timestamp.valueOf(LocalDateTime.now().plusDays(1)));
        tokenEntity.setUser(user);
        tokenEntity.setUsed(false);
        return tokenEntity;
    }

    /** Create a user entity with default values. */
    public static UserEntity createUser() {
        UserEntity user = new UserEntity();
        user.setEmail("user@example.com");
        user.setFullName("Test User");
        user.setPasswordHash(UUID.randomUUID().toString());
        return user;
    }
}
