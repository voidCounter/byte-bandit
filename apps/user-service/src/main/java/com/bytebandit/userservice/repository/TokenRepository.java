package com.bytebandit.userservice.repository;

import com.bytebandit.userservice.enums.TokenType;
import com.bytebandit.userservice.model.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, UUID> {

    Optional<TokenEntity> findByUserIdAndTypeAndCreatedAtBeforeAndExpiresAtAfterAndUsedIsFalse(
            UUID userId,
            TokenType type,
            Timestamp createdAt,
            Timestamp expiresAt
    );
}
