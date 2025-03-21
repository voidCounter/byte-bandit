package com.bytebandit.userservice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standardized API response structure.
 * status HTTP status code
 * message Response message
 * data Payload container
 * timestamp Response generation time
 * path Request URI path
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse {
    private int status;
    private String message;
    private Object data;
    private String timestamp;
    private String path;
}
