package com.bytebandit.fileservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class Chunks {

    @NotBlank
    private String id;

    @NotBlank
    private String status;
}
