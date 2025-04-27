package com.bytebandit.fileservice.dto;

import com.bytebandit.fileservice.enums.FileSystemPermission;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.UUID;
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
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
    private String password;
    @Future(message = "Expiration date must be in the future")
    private Timestamp expiresAt;
}
