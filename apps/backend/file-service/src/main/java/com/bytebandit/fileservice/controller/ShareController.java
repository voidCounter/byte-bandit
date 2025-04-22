package com.bytebandit.fileservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.slf4j.SLF4JLogger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ShareController {
    private final SLF4JLogger logger =
        (SLF4JLogger) org.slf4j.LoggerFactory.getLogger(ShareController.class);
    
    @GetMapping("/hello")
    public String hello(HttpServletRequest request) {
        logger.debug("userId: {}", request.getHeader("X-User=Id"));
        return "Found UserID";
    }
}
