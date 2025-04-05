package com.bytebandit.userservice.repository;

import com.bytebandit.userservice.model.TokenEntity;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;
import lib.user.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, UUID> {

    Optional<TokenEntity> findByUserIdAndTypeAndCreatedAtBeforeAndExpiresAtAfterAndUsedIsFalse(
        UUID userId,
        TokenType type,
        Timestamp createdAt,
        Timestamp expiresAt
    );
}
