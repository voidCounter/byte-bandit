package com.bytebandit.userservice.controller;

import com.bytebandit.userservice.service.UserEventProducer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lib.core.dto.response.ApiResponse;
import lib.core.events.UserEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
@Tag(
    name = "User event that is reponsible for sending user events to Kafka"
    + "which in turn inserts the data into snapshot table of the file service"
    + "or wherever a user snapshot is needed",
    description = "APIs for user events"
)
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
    @Operation(
        summary = "Create user event",
        description = "Creates a user event and sends it to Kafka."
    )
    @PostMapping("/create")
    public ApiResponse<Boolean> createUserEvent(@RequestBody UserEvent userEvent) {
        userEventProducer.sendUserEvent(userEvent);
        return ApiResponse.<Boolean>builder().data(true).message("Event will be sent"
                + " to Kafka")
            .timestamp(java.time.Instant.now().toString()).status(200).build();
    }
}
