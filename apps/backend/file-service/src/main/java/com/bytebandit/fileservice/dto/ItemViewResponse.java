package com.bytebandit.fileservice.dto;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemViewResponse {
    private UUID itemId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String ownerEmail;
    private String sharedByEmail;
    private String itemType;
    private Boolean isItemPasswordProtected;
    private String name;
    private String s3Url;
    private String mimeType;
    private String isStarred;
    private UUID parentId;
    private String permission;
    private BigInteger size;
    private List<ChildResponse> children;
}
