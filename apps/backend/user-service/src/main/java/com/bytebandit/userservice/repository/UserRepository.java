package com.bytebandit.userservice.repository;

import com.bytebandit.userservice.model.UserEntity;
import com.bytebandit.userservice.projection.CreateUserAndTokenProjection;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    @Query(
        value = "SELECT * FROM create_user_and_token(:email, :passwordHash, :fullName, :tokenHash, :tokenType, :expiresAt)",
        nativeQuery = true
    )
    CreateUserAndTokenProjection createUserAndToken(
        @Param("email") String email,
        @Param("passwordHash") String passwordHash,
        @Param("fullName") String fullName,
        @Param("tokenHash") String tokenHash,
        @Param("tokenType") String tokenType,
        @Param("expiresAt") java.sql.Timestamp expiresAt
    );
}
