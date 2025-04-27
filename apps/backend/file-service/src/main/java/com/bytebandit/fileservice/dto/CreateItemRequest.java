package com.bytebandit.fileservice.dto;

import com.bytebandit.fileservice.enums.FileSystemItemType;
import com.bytebandit.fileservice.validator.ValidId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.util.UUID;
import lib.core.validation.EnumValidator;
import lombok.Data;

@Data
public class CreateItemRequest {

    @Valid
    private Chunks chunks;

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

    private BigInteger size;

    @ValidId
    private String parentId;
}
