package com.bytebandit.gateway.clients;

import lib.core.dto.response.ApiResponse;
import lib.core.events.UserEvent;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    @PostMapping("/events/create")
    ApiResponse<Boolean> createUserEvent(@RequestBody UserEvent userEvent);
}
