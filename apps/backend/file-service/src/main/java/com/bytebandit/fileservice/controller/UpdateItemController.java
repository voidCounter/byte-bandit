package com.bytebandit.fileservice.controller;

import com.bytebandit.fileservice.dto.UpdateItemRequest;
import com.bytebandit.fileservice.utils.HttpHeaderUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lib.core.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/update")
public class UpdateItemController {

    @PostMapping("/rename")
    public ResponseEntity<ApiResponse<String>> renameItem(
        @Valid @RequestBody UpdateItemRequest request,
        @NotNull HttpServletRequest servletRequest
    ) {
        final String userId = HttpHeaderUtils.getUserIdHeader(servletRequest);

        return ResponseEntity.ok(
            ApiResponse.<String>builder()
                .status(200)
                .message("Renamed item successfully")
                .data()
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .path("/update/rename")
                .build()
        )
    }
}
