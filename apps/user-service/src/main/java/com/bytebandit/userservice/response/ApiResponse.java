package com.bytebandit.userservice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
