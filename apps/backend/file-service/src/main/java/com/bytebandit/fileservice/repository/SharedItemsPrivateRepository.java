package com.bytebandit.fileservice.repository;

import com.bytebandit.fileservice.model.SharedItemsPrivateEntity;
import com.bytebandit.fileservice.projection.ShareItemPrivateProjection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SharedItemsPrivateRepository extends
    JpaRepository<SharedItemsPrivateEntity, UUID> {

    @Query(
        value = "SELECT * FROM share_item_private"
            + "(:input_item_id, :shared_by_email, :input_shared_to_emails, :input_permissions)",
        nativeQuery = true
    )
    ShareItemPrivateProjection shareItemPrivate(
        @Param("input_item_id") UUID inputItemId,
        @Param("shared_by_email") String sharedByEmail,
        @Param("input_shared_to_emails") List<String> inputSharedToEmails,
        @Param("input_permissions") List<String> inputPermissions
    );
}
