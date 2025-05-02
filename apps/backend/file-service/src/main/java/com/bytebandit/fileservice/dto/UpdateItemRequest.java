package com.bytebandit.fileservice.dto;

import com.bytebandit.fileservice.validator.ValidId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

@Data
public class UpdateItemRequest {
    
    @NotBlank
    private String name;
    
    @NotNull
    @ValidId
    private String itemId;
}
