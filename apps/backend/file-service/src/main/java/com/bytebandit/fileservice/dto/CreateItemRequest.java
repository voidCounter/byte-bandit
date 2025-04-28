package com.bytebandit.fileservice.dto;

import com.bytebandit.fileservice.enums.FileSystemItemType;
import com.bytebandit.fileservice.validator.ValidId;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lib.core.validation.EnumValidator;
import lombok.Data;

@Data
public class CreateItemRequest {

    @Valid
    private JsonNode chunks;

    private String mimeType;

    @NotBlank
    private String name;

    private UUID ownerId;

    private String s3Url;

    private String status;

    @EnumValidator(
        enumClass = FileSystemItemType.class,
        message = "Invalid file system item type."
    )
    private String type;

    private Long size;

    @ValidId
    private String parentId;
}
