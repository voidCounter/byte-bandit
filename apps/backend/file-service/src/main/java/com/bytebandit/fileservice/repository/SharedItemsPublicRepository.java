package com.bytebandit.fileservice.repository;

import com.bytebandit.fileservice.model.SharedItemsPublicEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SharedItemsPublicRepository extends JpaRepository<SharedItemsPublicEntity, UUID> {
    @Query(value =
        "SELECT * FROM share_item_public(:p_user_id, :p_item_id, :p_permission, :p_password_hash)",
        nativeQuery = true)
    List<Object[]> callShareItemPublic(
        @Param("p_user_id") UUID userId,
        @Param("p_item_id") UUID itemId,
        @Param("p_permission") String permission,
        @Param("p_password_hash") String passwordHash
    );
}