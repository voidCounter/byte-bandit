package com.bytebandit.fileservice.repository;

import com.bytebandit.fileservice.model.FileSystemItemEntity;
import com.bytebandit.fileservice.projection.ItemViewProjection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FileSystemItemRepository extends JpaRepository<FileSystemItemEntity, UUID> {

    /**
     * Get the permission of a file system item recursively.
     *
     * @param inputItemId the ID of the item
     * @param inputUserId the ID of the user
     * @return the permission string
     */
    @Query(
        value = "SELECT * FROM get_permission_recursive("
            + "CAST(:input_item_id AS UUID), "
            + "CAST(:input_user_id AS UUID)"
            + ")",
        nativeQuery = true
    )
    String getPermissionRecursive(
        @Param("input_item_id") UUID inputItemId,
        @Param("input_user_id") UUID inputUserId
    );

    /**
     * Get the permission of a file system item.
     */
    @Query(
        value = "select * from item_view("
            + ":input_item_id, "
            + ":input_user_id, "
            + ":input_user_permission"
            + ")",
        nativeQuery = true
    )
    ItemViewProjection viewItems(
        @Param("input_item_id") UUID itemId,
        @Param("input_user_id") UUID userId,
        @Param("input_user_permission") String permission
    );

    /**
     * Get all items of a user.
     *
     * @param userId the ID of the user
     * @return the list of items
     */
    @Query(
        value = "select * from user_items("
            + ":input_user_id"
            + ")",
        nativeQuery = true
    )
    ItemViewProjection userItems(
        @Param("input_user_id") UUID userId
    );

    /**
     * Get all items shared with a user.
     *
     * @param uuid the ID of the user
     * @return the list of items
     */
    @Query(
        value = "select * from shared_with_user("
            + ":input_user_id"
            + ")",
        nativeQuery = true
    )
    List<ItemViewProjection> sharedWithUser(@Param("input_user_id") UUID uuid);
}
