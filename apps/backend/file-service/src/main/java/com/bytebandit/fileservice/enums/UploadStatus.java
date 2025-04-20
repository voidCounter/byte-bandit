package com.bytebandit.fileservice.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UploadStatus {

    NOT_UPLOADED("NOT_UPLOADED"),
    UPLOADED("UPLOADED");

    private final String status;
}
