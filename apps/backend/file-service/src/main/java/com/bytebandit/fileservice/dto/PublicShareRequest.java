package com.bytebandit.fileservice.dto;

import com.bytebandit.fileservice.enums.FileSystemPermission;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lib.core.validation.ValidPassword;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicShareRequest {
    @NotNull(message = "ItemID cannot be null")
    private UUID itemId;
    @JsonIgnore
    private UUID sharedBy;
    @NotNull(message = "Permission cannot be null")
    private FileSystemPermission permission;
    @ValidPassword(message = "Password must be at least 8 characters long")
    private String password;
}
