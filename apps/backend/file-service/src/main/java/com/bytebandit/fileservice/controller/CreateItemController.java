package com.bytebandit.fileservice.controller;

import static com.bytebandit.fileservice.utils.HttpHeaderUtils.getUserIdHeader;

import com.bytebandit.fileservice.dto.CreateItemRequest;
import com.bytebandit.fileservice.dto.CreateItemResponse;
import com.bytebandit.fileservice.service.CreateItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lib.core.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/create")
@RequiredArgsConstructor
@Tag(
    name = "Create Item",
    description = "Create item operations"
)
public class CreateItemController {

    private final CreateItemService createItemService;

    /**
     * Creates a new file system item.
     */
    @Operation(
        summary = "Create item",
        description = "Handles the request to create a new file system item."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<CreateItemResponse>> createItem(
        @Valid @RequestBody CreateItemRequest request,
        @NotNull HttpServletRequest servletRequest
    ) {
        request.setOwnerId(
            UUID.fromString(
                getUserIdHeader(servletRequest)
            )
        );
        return ResponseEntity.ok(
            ApiResponse.<CreateItemResponse>builder()
                .status(200)
                .message("Created item successfully")
                .data(createItemService.createItem(request))
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .path("/create")
                .build()
        );
    }
}
