package com.bytebandit.gateway.dto;

public record AuthenticatedUserDto(
        String email,
        String fullName
) {
}

