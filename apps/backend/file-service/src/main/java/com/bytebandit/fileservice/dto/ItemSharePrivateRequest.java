package com.bytebandit.fileservice.dto;

import com.bytebandit.fileservice.validator.ValidId;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemSharePrivateRequest {

    @NotNull
    @ValidId
    private String itemId;

    @NotNull
    private List<String> sharedTo;

    @NotNull
    private String sharedBy;

    @NotNull
    private List<String> permissions;
}
