package com.bytebandit.userservice.repository;

import com.bytebandit.userservice.model.TokenEntity;
import com.bytebandit.userservice.model.UserEntity;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;
import lib.user.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, UUID> {

    Optional<TokenEntity> findByUserIdAndTypeAndCreatedAtBeforeAndExpiresAtAfterAndUsedIsFalse(
        UUID userId,
        TokenType type,
        Timestamp createdAt,
        Timestamp expiresAt
    );

    @Modifying
    @Query("Update TokenEntity t set t.used = true where t.user= :user and t.type = :type and"
        + " t.used = false")
    void invalidateAllForUserAndType(@Param("user") UserEntity user,
                                     @Param("type") TokenType type);
}
