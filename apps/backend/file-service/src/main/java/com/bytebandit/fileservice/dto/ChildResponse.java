package com.bytebandit.fileservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChildResponse {

    @JsonProperty("item_id")
    private UUID itemId;

    @JsonProperty("created_at")
    private Timestamp createdAt;

    @JsonProperty("updated_at")
    private Timestamp updatedAt;

    @JsonProperty("owner_email")
    private String ownerEmail;

    @JsonProperty("shared_by_email")
    private String sharedByEmail;

    @JsonProperty("item_type")
    private String itemType;

    @JsonProperty("is_item_password_protected")
    private Boolean isItemPasswordProtected;

    @JsonProperty("name")
    private String name;

    @JsonProperty("s3url")
    private String s3Url;

    @JsonProperty("mime_type")
    private String mimeType;

    @JsonProperty("is_starred")
    private String isStarred;

    @JsonProperty("parent_id")
    private UUID parentId;

    @JsonProperty("size")
    private BigInteger size;

    @JsonProperty("permission")
    private String permission;
}
