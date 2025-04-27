package com.bytebandit.fileservice.controller;

import static com.bytebandit.fileservice.utils.HttpHeaderUtils.getUserIdHeader;

import com.bytebandit.fileservice.dto.CreateItemRequest;
import com.bytebandit.fileservice.dto.CreateItemResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lib.core.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/create")
public class CreateItemController {

    /**
     * Creates a new file system item.
     */
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
        
    }
}
