package com.bytebandit.fileservice.controller;

import com.bytebandit.fileservice.dto.PublicShareRequest;
import com.bytebandit.fileservice.dto.PublicShareResponse;
import com.bytebandit.fileservice.service.ShareService;
import jakarta.servlet.http.HttpServletRequest;
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
public class ShareController {
    private final ShareService shareService;
    
    public ShareController(ShareService shareService) {
        this.shareService = shareService;
    }
    
    @PostMapping("/public")
    ResponseEntity<ApiResponse<PublicShareResponse>> sharePublic(
        @RequestBody PublicShareRequest request, @NotNull HttpServletRequest httpServletRequest) {
        request.setSharedBy(UUID.fromString(httpServletRequest.getHeader("X-User-Id")));
        return shareService.sharePublic(request);
    }
}
