package com.bytebandit.userservice.repository;

import com.bytebandit.userservice.model.UserEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository for the user entity. */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
}
