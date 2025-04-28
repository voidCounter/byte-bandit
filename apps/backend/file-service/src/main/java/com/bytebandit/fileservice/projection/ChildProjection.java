package com.bytebandit.fileservice.projection;

import java.sql.Timestamp;
import java.util.UUID;

public interface ChildProjection {
    UUID getItemId();

    Timestamp getCreatedAt();

    Timestamp getUpdatedAt();

    String getOwnerEmail();

    String getSharedByEmail();

    String getItemType();

    Boolean getIsItemPasswordProtected();

    String getName();

    String getS3Url();

    String getMimeType();

    String getIsStarred();

    UUID getParentId();

    String getPermission();
}
