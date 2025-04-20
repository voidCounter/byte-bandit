package com.bytebandit.fileservice.dto;

import com.bytebandit.fileservice.enums.FileSystemItemType;
import com.bytebandit.fileservice.enums.UploadStatus;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.UUID;
import lombok.Data;

@Data
public class FileSystemItemRequest {
    private String name;
    private Long size;
    private String mimeType;
    private UUID owner;
    private UploadStatus status;
    private FileSystemItemType type;
    private JsonNode chunks;
    private String s3Url;
    private UUID parentId;
}