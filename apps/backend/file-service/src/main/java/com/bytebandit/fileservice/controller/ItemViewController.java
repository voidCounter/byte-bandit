package com.bytebandit.fileservice.controller;

import com.bytebandit.fileservice.dto.ItemViewRequest;
import com.bytebandit.fileservice.dto.ItemViewResponse;
import com.bytebandit.fileservice.service.ItemViewService;
import com.bytebandit.fileservice.utils.HttpHeaderUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import lib.core.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/view")
@Tag(name = "Item View", description = "Item view operations")
public class ItemViewController {

    private final ItemViewService itemViewService;

    /**
     * Handles the item view request and returns the item view response.
     *
     * @param request the item view request
     * @param servletRequest the HTTP servlet request
     *
     * @return the item view response
     */
    @Operation(
        summary = "View item",
        description = "Handles the request to view an item. Returns the item view response."
    )
    @PostMapping
    public ResponseEntity<ApiResponse<ItemViewResponse>> viewItem(
        @Valid @RequestBody ItemViewRequest request,
        @NotNull HttpServletRequest servletRequest
    ) {
        final String userId = HttpHeaderUtils.getUserIdHeader(servletRequest);
        final ItemViewResponse response = itemViewService.getItemView(
            request,
            UUID.fromString(userId)
        );

        return ResponseEntity.ok(ApiResponse.<ItemViewResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Viewed item successfully")
                .data(response)
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .build());
    }

    /**
     * Handles the request to get all items of a user.
     *
     * @return the response entity containing the item view response
     */
    @Operation(
        summary = "Get all items of a user",
        description = "Retrieves all items associated with the authenticated user."
    )
    @GetMapping
    public ResponseEntity<ApiResponse<ItemViewResponse>> getUserItems(
        @NotNull HttpServletRequest servletRequest
    ) {
        final String userId = HttpHeaderUtils.getUserIdHeader(servletRequest);
        return ResponseEntity.ok(
            ApiResponse.<ItemViewResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Retrieved all items of the user successfully")
                .data(itemViewService.getUserItems(userId))
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .build()
        );
    }

    /**
     * Handles the request to get all items shared with the user.
     *
     * @return the response entity containing the item view response
     */
    @Operation(
        summary = "Get all items shared with the user",
        description = "Retrieves all items that are shared with the authenticated user."
    )
    @GetMapping("/shared-with-me")
    public ResponseEntity<ApiResponse<List<ItemViewResponse>>> getSharedWithMe(
        @NotNull HttpServletRequest servletRequest
    ) {
        final String userId = HttpHeaderUtils.getUserIdHeader(servletRequest);
        return ResponseEntity.ok(
            ApiResponse.<List<ItemViewResponse>>builder()
                .status(HttpStatus.OK.value())
                .message("Retrieved all items shared with the user successfully")
                .data(itemViewService.getSharedWithMe(userId))
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .build()
        );
    }

}
