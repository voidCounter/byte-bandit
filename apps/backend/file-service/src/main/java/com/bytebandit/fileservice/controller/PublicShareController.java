package com.bytebandit.fileservice.controller;

import com.bytebandit.fileservice.dto.PublicShareRequest;
import com.bytebandit.fileservice.dto.PublicShareResponse;
import com.bytebandit.fileservice.service.PublicShareService;
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
@RequestMapping("/share")
public class PublicShareController {
    private final PublicShareService shareService;
    
    public PublicShareController(PublicShareService shareService) {
        this.shareService = shareService;
    }
    
    @PostMapping("/public")
    ResponseEntity<ApiResponse<PublicShareResponse>> sharePublic(
        @Valid @RequestBody PublicShareRequest request,
        @NotNull HttpServletRequest httpServletRequest) {
        request.setSharedBy(UUID.fromString(httpServletRequest.getHeader("X-User-Id")));
        return shareService.sharePublic(request);
    }
}
