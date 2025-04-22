package com.bytebandit.fileservice.service;

import com.bytebandit.fileservice.model.UserSnapshotEntity;
import com.bytebandit.fileservice.repository.UserSnapshotRepository;
import java.util.UUID;
import lib.core.enums.UserAction;
import lib.core.events.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserEventConsumer {
    private static final Logger logger = LoggerFactory.getLogger(UserEventConsumer.class);
    private final UserSnapshotRepository userSnapshotRepository;
    
    public UserEventConsumer(UserSnapshotRepository userSnapshotRepository) {
        this.userSnapshotRepository = userSnapshotRepository;
    }
    
    /**
     * Consumes UserEvent messages from the Kafka topic "user-events".
     *
     * @param userEvent the UserEvent message
     */
    @KafkaListener(topics = "user-events", groupId = "file-service-group")
    public void consumeUserEvent(UserEvent userEvent) {
        if (userEvent.getAction() == UserAction.USER_VERIFIED) {
            createUserSnapshot(userEvent.getUserId(), userEvent.getEmail());
        }
    }
    
    private void createUserSnapshot(UUID userId, String email) {
        if (!userSnapshotRepository.existsById(userId)) {
            UserSnapshotEntity userSnapshot = UserSnapshotEntity.builder()
                .userId(userId)
                .email(email)
                .build();
            
            userSnapshotRepository.save(userSnapshot);
            logger.debug("Created user snapshot: userId={}, email={}", userId, email);
        } else {
            logger.debug("User snapshot already exists: userId={}", userId);
        }
    }
}

