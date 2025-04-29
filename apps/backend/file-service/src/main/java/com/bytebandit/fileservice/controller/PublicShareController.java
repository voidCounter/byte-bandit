package com.bytebandit.fileservice.controller;

import com.bytebandit.fileservice.dto.PublicShareRequest;
import com.bytebandit.fileservice.dto.PublicShareResponse;
import com.bytebandit.fileservice.exception.UnauthenticatedException;
import com.bytebandit.fileservice.service.PublicShareService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lib.core.dto.response.ApiResponse;
import lib.core.enums.CustomHttpHeader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping("/share")
public class PublicShareController {
    private final PublicShareService shareService;
    
    public PublicShareController(PublicShareService shareService) {
        this.shareService = shareService;
    }
    
    /**
     * Share item with public permission.
     *
     * @param request            DTO of the request
     * @param httpServletRequest HttpServletRequest
     *
     * @return ResponseEntity with ApiResponse containing the public share response.
     */
    @PostMapping("/public")
    ResponseEntity<ApiResponse<PublicShareResponse>> sharePublic(
        @Valid @RequestBody PublicShareRequest request,
        @NotNull HttpServletRequest httpServletRequest) {
        String userIdHeader = httpServletRequest.getHeader(CustomHttpHeader.USER_ID.getValue());
        if (userIdHeader == null) {
            throw new UnauthenticatedException("User ID header is missing.");
        }
        try {
            request.setSharedBy(UUID.fromString(userIdHeader));
        } catch (IllegalArgumentException ex) {
            throw new HttpClientErrorException(
                org.springframework.http.HttpStatus.BAD_REQUEST,
                "Invalid User ID format."
            );
        }
        return shareService.sharePublic(request);
    }
}
