package com.bytebandit.userservice.controller;

import com.bytebandit.userservice.service.UserEventProducer;
import lib.core.events.UserEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
public class EventController {
    private final UserEventProducer userEventProducer;
    
    public EventController(UserEventProducer userEventProducer) {
        this.userEventProducer = userEventProducer;
    }
    
    @PostMapping("/create")
    public String createEvent(@RequestBody UserEvent userEvent) {
        userEventProducer.sendUserEvent(userEvent);
        return "Event created successfully";
    }
}
