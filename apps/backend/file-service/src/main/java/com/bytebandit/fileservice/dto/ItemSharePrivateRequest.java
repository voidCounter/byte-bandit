package com.bytebandit.fileservice.dto;

import com.bytebandit.fileservice.validator.ValidId;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemSharePrivateRequest {

    @NotBlank
    @ValidId
    private String itemId;

    @NotBlank
    private List<String> sharedTo;

    @NotBlank
    private String sharedType;

    @NotBlank
    @ValidId
    private String sharedBy;

    @NotBlank
    private List<String> permissions;
}
