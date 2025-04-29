package com.bytebandit.gateway.repository;

import com.bytebandit.gateway.model.TokenEntity;
import com.bytebandit.gateway.model.UserEntity;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lib.user.enums.TokenType;
import org.antlr.v4.runtime.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, UUID> {
    
    Optional<TokenEntity> findByUserIdAndType(UUID userId, TokenType type);
    
    List<TokenEntity> findAllByUserIdAndTypeAndUsed(UUID userId, TokenType type, boolean used);
}
