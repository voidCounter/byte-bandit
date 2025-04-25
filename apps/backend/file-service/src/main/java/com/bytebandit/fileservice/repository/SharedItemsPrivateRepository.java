package com.bytebandit.fileservice.repository;

import com.bytebandit.fileservice.model.SharedItemsPrivateEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SharedItemsPrivateRepository extends
    JpaRepository<SharedItemsPrivateEntity, UUID> {

    @Query(
        value = "SELECT * FROM share_item_private("
            + "CAST(:input_item_id AS UUID), "
            + "CAST(:shared_by_email AS TEXT), "
            + "CAST(:input_shared_to_emails AS TEXT[]), "
            + "CAST(:input_permissions AS TEXT[]))",
        nativeQuery = true
    )
    String[] shareItemPrivate(
        @Param("input_item_id") UUID inputItemId,
        @Param("shared_by_email") String sharedByEmail,
        @Param("input_shared_to_emails") String[] inputSharedToEmails,
        @Param("input_permissions") String[] inputPermissions
    );
}
