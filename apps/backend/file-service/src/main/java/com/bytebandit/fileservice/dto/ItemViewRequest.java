package com.bytebandit.fileservice.dto;

import com.bytebandit.fileservice.validator.ValidId;
import lombok.Data;

@Data
public class ItemViewRequest {

    @ValidId
    private String itemId;

    private String password;
}
