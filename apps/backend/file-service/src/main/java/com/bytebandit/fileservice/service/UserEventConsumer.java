package com.bytebandit.fileservice.service;

import lib.core.enums.UserAction;
import lib.core.events.UserEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class UserEventConsumer {
    private final UserEventHandlers userEventHandlers;
    
    public UserEventConsumer(
        UserEventHandlers userEventHandlers) {
        this.userEventHandlers = userEventHandlers;
    }
    
    /**
     * Consumes UserEvent messages from the Kafka topic "user-events".
     *
     * @param userEvent the UserEvent message
     */
    @KafkaListener(topics = "user-events", groupId = "file-service-group",
        errorHandler = "kafkaErrorHandler")
    public void consumeUserEvent(@Payload UserEvent userEvent,
                                 @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        if (userEvent.getAction() == UserAction.USER_VERIFIED) {
            userEventHandlers.createUserSnapshot(userEvent.getUserId(), userEvent.getEmail());
        }
    }
}

