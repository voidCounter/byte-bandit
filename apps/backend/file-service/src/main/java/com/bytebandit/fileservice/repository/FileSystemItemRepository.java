package com.bytebandit.fileservice.repository;

import com.bytebandit.fileservice.model.FileSystemItemEntity;
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
}
