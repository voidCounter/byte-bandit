package com.bytebandit.fileservice.repository;

import com.bytebandit.fileservice.model.SharedItemsPublicEntity;
import com.bytebandit.fileservice.projection.SharedItemPublicProjection;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SharedItemsPublicRepository extends JpaRepository<SharedItemsPublicEntity, UUID> {
    @Query(value =
        "SELECT * FROM share_item_public(:p_user_id, :p_item_id, :p_permission, :p_permission_level"
            + ", :p_password_hash, :p_expires_at)",
        nativeQuery = true)
    List<SharedItemPublicProjection> callShareItemPublic(
        @Param("p_user_id") UUID userId,
        @Param("p_item_id") UUID itemId,
        @Param("p_permission") String permission,
        @Param("p_permission_level") String permissionLevel,
        @Param("p_password_hash") String passwordHash,
        @Param("p_expires_at") Timestamp expiresAt
    );

    SharedItemsPublicEntity findByItemId(UUID uuid);

    SharedItemsPublicEntity findByItemIdAndExpiresAtIsBefore(UUID itemId, Timestamp from);
}