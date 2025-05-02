package com.bytebandit.fileservice.dto;

import com.bytebandit.fileservice.validator.ValidId;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MoveItemRequest {

    @ValidId
    @NotNull
    private String itemId;

    @ValidId
    @NotNull
    private String parentId;
}
