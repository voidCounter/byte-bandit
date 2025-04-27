package com.bytebandit.fileservice.dto;

import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateItemResponse {

    private String itemId;
    private String name;
    private String createdAt;
    private String updatedAt;
    private BigInteger fileSize;
    private String status;
    private String type;
    private String parentId;
}
