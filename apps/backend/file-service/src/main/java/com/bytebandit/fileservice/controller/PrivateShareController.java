package com.bytebandit.fileservice.controller;

import com.bytebandit.fileservice.dto.ItemSharePrivateRequest;
import com.bytebandit.fileservice.dto.ItemSharePrivateResponse;
import com.bytebandit.fileservice.service.PrivatePermissionService;
import jakarta.validation.Valid;
import lib.core.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/share/private")
@RequiredArgsConstructor
public class PrivateShareController {

    private final PrivatePermissionService privatePermissionService;

    /**
     * Share item with private permission.
     * @param request
     * @return
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ItemSharePrivateResponse>> sharePrivate(
        @Valid @RequestBody ItemSharePrivateRequest request
    ) {
        ItemSharePrivateResponse permissionResponse =
            privatePermissionService.givePermissionToUsers(request);

        ApiResponse<ItemSharePrivateResponse> response = ApiResponse.<ItemSharePrivateResponse>builder()
            .status(HttpStatus.OK.value())
            .message("Shared item successfully")
            .data(permissionResponse)
            .timestamp(String.valueOf(System.currentTimeMillis()))
            .path("/share/private")
            .build();

        return ResponseEntity.ok(response);
    }
}
