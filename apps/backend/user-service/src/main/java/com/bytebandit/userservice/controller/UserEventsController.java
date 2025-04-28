package com.bytebandit.userservice.controller;

import com.bytebandit.userservice.service.UserEventProducer;
import lib.core.dto.response.ApiResponse;
import lib.core.events.UserEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
public class UserEventsController {
    private final UserEventProducer userEventProducer;
    
    public UserEventsController(UserEventProducer userEventProducer) {
        this.userEventProducer = userEventProducer;
    }
    
    /**
     * Creates a user event and sends it to Kafka.
     *
     * @param userEvent The user event to be created.
     *
     * @return ApiResponse indicating the success of the operation.
     */
    @PostMapping("/create")
    public ApiResponse<Boolean> createUserEvent(@RequestBody UserEvent userEvent) {
        userEventProducer.sendUserEvent(userEvent);
        return ApiResponse.<Boolean>builder().data(true).message("Event will be sent"
                + " to Kafka")
            .timestamp(java.time.Instant.now().toString()).status(200).build();
    }
}
