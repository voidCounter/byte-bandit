package com.bytebandit.fileservice.controller;

import static com.bytebandit.fileservice.utils.HttpHeaderUtils.getUserIdHeader;

import com.bytebandit.fileservice.dto.ItemSharePrivateRequest;
import com.bytebandit.fileservice.dto.ItemSharePrivateResponse;
import com.bytebandit.fileservice.service.PrivatePermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lib.core.dto.response.ApiResponse;
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
@Tag(
    name = "Private Share",
    description = "Private share operations"
)
public class PrivateShareController {

    private final PrivatePermissionService privatePermissionService;

    /**
     * Share item with private permission.
     */
    @Operation(
        summary = "Share item with private permission",
        description = "Handles the request to share an item with private permission."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<ItemSharePrivateResponse>> sharePrivate(
        @Valid @RequestBody ItemSharePrivateRequest request,
        @NotNull HttpServletRequest servletRequest
    ) {

        request.setSharedByUserId(getUserIdHeader(servletRequest));
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
