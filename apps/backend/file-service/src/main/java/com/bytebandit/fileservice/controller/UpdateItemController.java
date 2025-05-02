package com.bytebandit.fileservice.controller;

import com.bytebandit.fileservice.dto.MoveItemRequest;
import com.bytebandit.fileservice.dto.UpdateItemRequest;
import com.bytebandit.fileservice.service.UpdateItemService;
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
    
    private final UpdateItemService updateItemService;
    
    public UpdateItemController(UpdateItemService updateItemService) {
        this.updateItemService = updateItemService;
    }

    /**
     * Handles the request to rename an item.
     *
     * @param request the request containing the item ID and new name
     * @param servletRequest the HTTP servlet request
     *
     * @return a response entity containing the result of the rename operation
     */
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
                .data(updateItemService.updateItem(request, userId))
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .path("/update/rename")
                .build()
        );
    }

    /**
     * Handles the request to move an item.
     *
     * @param request the request containing the item ID and new location
     * @param servletRequest the HTTP servlet request
     *
     * @return a response entity containing the result of the move operation
     */
    @PostMapping("/move")
    public ResponseEntity<ApiResponse<String>> moveItem(
        @Valid @RequestBody MoveItemRequest request,
        @NotNull HttpServletRequest servletRequest
    ) {
        final String userId = HttpHeaderUtils.getUserIdHeader(servletRequest);

        return ResponseEntity.ok(
            ApiResponse.<String>builder()
                .status(200)
                .message("Moved item successfully")
                .data(updateItemService.moveItem(request, userId))
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .path("/update/move")
                .build()
        );
    }
}
