package com.bytebandit.fileservice.dto;

import com.bytebandit.fileservice.validator.ListSizeEqual;
import com.bytebandit.fileservice.validator.ValidId;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ListSizeEqual(
    list1FieldName = "sharedTo",
    list2FieldName = "permissions",
    message = "The sizes of the lists must be the same."
)
public class ItemSharePrivateRequest {

    @NotNull
    @ValidId
    private String itemId;

    @NotNull
    @Size(min = 1, message = "At least one email must be provided")
    private List<@Email(message = "Invalid Email Format") String> sharedTo;

    @ValidId
    private UUID sharedByUserId;

    @NotNull
    @Size(min = 1, message = "At least one permission must be provided")
    private List<String> permissions;
}
