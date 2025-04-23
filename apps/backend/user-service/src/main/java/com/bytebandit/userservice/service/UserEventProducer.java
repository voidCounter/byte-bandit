package com.bytebandit.userservice.service;

import java.util.concurrent.CompletableFuture;
import lib.core.events.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class UserEventProducer {
    private final Logger logger = LoggerFactory.getLogger(UserEventProducer.class);
    private static final String TOPIC = "user-events";
    
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;
    
    public UserEventProducer(KafkaTemplate<String, UserEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    /**
     * Sends a user event to the Kafka topic.
     *
     * @param userEvent the user event to send
     */
    public void sendUserEvent(UserEvent userEvent) {
        kafkaTemplate.send(TOPIC, userEvent.getUserId().toString(), userEvent);
        String key = userEvent.getUserId().toString();
        CompletableFuture<SendResult<String, UserEvent>> future = kafkaTemplate.send(TOPIC, key,
            userEvent);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                logger.error("Failed to send event for userId={}", key, ex);
            } else {
                logger.debug("Sent event for userId={}", key);
            }
        });
    }
}
