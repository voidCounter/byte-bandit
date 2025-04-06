package com.bytebandit.gateway.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CookieKey {

    ACCESS_TOKEN("access_token"),
    REFRESH_TOKEN("refresh_token");

    private final String key;
}
