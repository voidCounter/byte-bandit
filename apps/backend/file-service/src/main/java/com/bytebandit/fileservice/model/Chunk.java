package com.bytebandit.fileservice.model;

import java.util.UUID;
import lombok.Data;

@Data
public class Chunk {
    private UUID id;
    private String status;
}
