package com.bytebandit.fileservice.projection;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public interface ItemViewProjection {
    UUID getOutputItemId();

    Timestamp getOutputCreatedAt();

    Timestamp getOutputUpdatedAt();

    String getOutputOwnerEmail();

    String getOutputSharedByEmail();

    String getOutputItemType();

    Boolean getOutputIsItemPasswordProtected();

    String getOutputName();

    String getOutputS3Url();

    String getOutputMimeType();

    String getOutputIsStarred();

    UUID getOutputParentId();

    String getOutputPermission();

    String getOutputChildren();
}