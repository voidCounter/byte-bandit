package com.bytebandit.fileservice.controller;

import com.bytebandit.fileservice.dto.ItemSharePrivateRequest;
import com.bytebandit.fileservice.dto.ItemSharePrivateResponse;
import com.bytebandit.fileservice.exception.UnauthenticatedException;
import com.bytebandit.fileservice.service.PrivatePermissionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lib.core.dto.response.ApiResponse;
import lib.core.enums.CustomHttpHeader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/share/private")
@RequiredArgsConstructor
@Slf4j
public class PrivateShareController {

    private final PrivatePermissionService privatePermissionService;

    /**
     * Share item with private permission.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ItemSharePrivateResponse>> sharePrivate(
        @Valid @RequestBody ItemSharePrivateRequest request,
        @NotNull HttpServletRequest servletRequest
    ) {

        String userIdHeader = servletRequest.getHeader(CustomHttpHeader.USER_ID.getValue());

        if (userIdHeader == null) {
            throw new UnauthenticatedException("User ID header is missing");
        }

        log.info("Shared by user id : {}", userIdHeader);

        request.setSharedByUserId(userIdHeader);
        ItemSharePrivateResponse permissionResponse =
            privatePermissionService.givePermissionToUsers(request);


        ApiResponse<ItemSharePrivateResponse> response
            = ApiResponse.<ItemSharePrivateResponse>builder()
            .status(HttpStatus.OK.value())
            .message("Shared item successfully")
            .data(permissionResponse)
            .timestamp(String.valueOf(System.currentTimeMillis()))
            .path("/share/private")
            .build();
        log.debug("{}", response.getData());
        return ResponseEntity.ok(response);
    }
}
