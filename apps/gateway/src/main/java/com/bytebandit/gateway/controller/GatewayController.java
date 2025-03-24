package com.bytebandit.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class GatewayController {
    @GetMapping("/csrf")
    public ResponseEntity<?> csrf() {
        return ResponseEntity.ok().build();
    }
}
