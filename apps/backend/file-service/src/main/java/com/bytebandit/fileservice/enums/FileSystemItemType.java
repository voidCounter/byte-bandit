package com.bytebandit.fileservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileSystemItemType {
    FILE("file"),
    FOLDER("folder");

    private final String type;
}
