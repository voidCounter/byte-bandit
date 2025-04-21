package com.bytebandit.fileservice.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileSystemPermission {

    VIEWER("viewer"),
    EDITOR("editor");

    private final String permission;
}
