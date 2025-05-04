package com.bytebandit.fileservice.dto;

import com.bytebandit.fileservice.validator.ValidId;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NonNull;

@Data
public class UpdateItemRequest {

    @NotBlank
    private String name;

    @NonNull
    @ValidId
    private String itemId;
}
