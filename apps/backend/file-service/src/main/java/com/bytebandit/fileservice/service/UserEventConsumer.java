package com.bytebandit.fileservice.service;

import com.bytebandit.fileservice.model.UserSnapshotEntity;
import com.bytebandit.fileservice.repository.UserSnapshotRepository;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lib.core.enums.UserAction;
import lib.core.events.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class UserEventConsumer {
    private static final Logger logger = LoggerFactory.getLogger(UserEventConsumer.class);
    private final UserEventHandlers userEventHandlers;
    
    public UserEventConsumer(UserSnapshotRepository userSnapshotRepository,
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

