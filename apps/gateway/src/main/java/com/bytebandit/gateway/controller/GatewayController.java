package com.bytebandit.gateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/gateway")
public class GatewayController {
    @RequestMapping("/")
    public String index() {
        return "Hello World!";
    }
}
