package com.bytebandit.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class GatewayController {
    /**
     * Handles a GET request to retrieve a response containing csrf token in the cookies.
     * The CSRF token is automatically set in the XSRF-TOKEN cookie by Spring Security's
     * CsrfFilter when this endpoint is accessed.
     * @return ResponseEntity with a successful HTTP status.
     */
    @GetMapping("/csrf")
    public ResponseEntity<?> csrf() {
        return ResponseEntity.ok().build();
    }

    /**
     * Handles a POST request to validate CSRF protection.
     * @return ResponseEntity containing a message indicating the CSRF test result.
     */
    @PostMapping("/test-csrf")
    public ResponseEntity<String> testCsrf() {
        return ResponseEntity.ok("CSRF test passed");
    }
}
