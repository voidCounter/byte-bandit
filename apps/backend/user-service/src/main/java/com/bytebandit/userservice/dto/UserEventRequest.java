package com.bytebandit.userservice.dto;

import lombok.Data;

@Data
public class UserEventRequest {
    private String userId;
    private String eventType;
    private String eventData;
    
    public UserEventRequest(String userId, String eventType, String eventData) {
        this.userId = userId;
        this.eventType = eventType;
        this.eventData = eventData;
    }
}
