package com.bytebandit.gateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Internal", description = "APIs for csrf token and test")
public class GatewayController {
    /**
     * Handles a GET request to retrieve a response containing csrf token in the cookies. The CSRF
     * token is automatically set in the XSRF-TOKEN cookie by Spring Security's CsrfFilter when this
     * endpoint is accessed.
     *
     * @return ResponseEntity with a successful HTTP status.
     */
    @Operation(
        summary = "CSRF token",
        description = "Returns the CSRF token. The CSRF token is automatically set in "
            + "the XSRF-TOKEN "
            + "cookie by Spring Security's CsrfFilter when this endpoint is accessed."
    )
    @GetMapping("/csrf")
    public ResponseEntity<String> csrf(
        @RequestAttribute(name = "_csrf", required = false) CsrfToken csrfToken) {
        if (csrfToken != null) {
            return ResponseEntity.ok(csrfToken.getToken());
        }
        return ResponseEntity.badRequest().body("CSRF token not found");
    }
    
    /**
     * Handles a POST request to validate CSRF protection.
     *
     * @return ResponseEntity containing a message indicating the CSRF test result.
     */
    @PostMapping("/test-csrf")
    public ResponseEntity<String> testCsrf() {
        return ResponseEntity.ok("CSRF test passed");
    }
}
