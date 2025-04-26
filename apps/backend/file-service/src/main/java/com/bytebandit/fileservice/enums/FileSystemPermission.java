package com.bytebandit.fileservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileSystemPermission {
    
    VIEWER("viewer"),
    EDITOR("editor");
    
    private final String permission;
    
    @JsonValue
    public String getValue() {
        return name().toLowerCase();
    }
    
    @JsonCreator
    public static FileSystemPermission fromValue(String value) {
        for (FileSystemPermission permission : FileSystemPermission.values()) {
            if (permission.permission.equalsIgnoreCase(value)) {
                return permission;
            }
        }
        throw new IllegalArgumentException("Invalid permission: " + value);
    }
    
}
