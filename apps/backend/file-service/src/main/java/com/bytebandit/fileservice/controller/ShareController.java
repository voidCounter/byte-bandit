package com.bytebandit.fileservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShareController {
    private final Logger logger = LoggerFactory.getLogger(ShareController.class);
    
    @GetMapping("/hello")
    public String hello(HttpServletRequest request) {
        logger.debug("userId: {}", request.getHeader("X-User-Id"));
        return "Found UserID";
    }
}
