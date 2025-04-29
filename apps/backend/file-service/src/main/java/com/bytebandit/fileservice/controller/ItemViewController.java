package com.bytebandit.fileservice.controller;

import com.bytebandit.fileservice.dto.ItemViewRequest;
import com.bytebandit.fileservice.dto.ItemViewResponse;
import com.bytebandit.fileservice.service.ItemViewService;
import com.bytebandit.fileservice.utils.HttpHeaderUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lib.core.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/view")
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
}
