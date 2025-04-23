package com.bytebandit.fileservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bytebandit.fileservice.model.UserSnapshotEntity;
import com.bytebandit.fileservice.repository.UserSnapshotRepository;
import java.util.UUID;
import lib.core.enums.UserAction;
import lib.core.events.UserEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

class UserEventConsumerTest {
    
    @Mock
    private UserSnapshotRepository userSnapshotRepository;
    
    @Mock
    private Logger logger;
    
    @InjectMocks
    private UserEventConsumer userEventConsumer;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    /**
     * Test the consumeUserEvent method when the user event is USER_VERIFIED and the user snapshot
     * does not exist.
     */
    @Test
    void consumeUserEvent_UserVerified_CreatesUserSnapshot() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        UserEvent userEvent =
            UserEvent.builder().userId(userId).email(email).action(UserAction.USER_VERIFIED)
                .build();
        when(userSnapshotRepository.existsById(userId)).thenReturn(false);
        
        userEventConsumer.consumeUserEvent(userEvent);
        
        // assert
        ArgumentCaptor<UserSnapshotEntity> userSnapshotCaptor =
            ArgumentCaptor.forClass(UserSnapshotEntity.class);
        verify(userSnapshotRepository, times(1)).save(userSnapshotCaptor.capture());
        
        UserSnapshotEntity savedUserSnapshot = userSnapshotCaptor.getValue();
        assertEquals(userId, savedUserSnapshot.getUserId());
        assertEquals(email, savedUserSnapshot.getEmail());
    }
    
    /**
     * Verifies that the `consumeUserEvent` method does not create a user snapshot when the user
     * event action is `USER_VERIFIED` and the user snapshot already exists in the database.
     *
     * <p>
     * Preconditions: - A `USER_VERIFIED` event is received. - A user snapshot with the
     * corresponding user ID already exists in the repository.
     *
     * <p>
     * Expected Behavior: - The `consumeUserEvent` method does not attempt to save a new user
     * snapshot. - There are no calls to the `userSnapshotRepository.save()` method.
     */
    @Test
    void consumeUserEvent_UserVerified_UserSnapshotExists_DoesNotCreateUserSnapshot() {
        // Arrange
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        UserEvent userEvent =
            UserEvent.builder().userId(userId).email(email).action(UserAction.USER_VERIFIED)
                .build();
        
        when(userSnapshotRepository.existsById(userId)).thenReturn(true);
        
        // Act
        userEventConsumer.consumeUserEvent(userEvent);
        
        // Assert
        verify(userSnapshotRepository, never()).save(any());
    }
    
    /**
     * Test the consumeUserEvent method when the user event is not USER_VERIFIED.
     */
    @Test
    void consumeUserEvent_NotUserVerified_DoesNotCreateUserSnapshot() {
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        UserEvent userEvent =
            UserEvent.builder().userId(userId).email(email).action(UserAction.USER_REGISTERED)
                .build();
        
        userEventConsumer.consumeUserEvent(userEvent);
        
        verify(userSnapshotRepository, never()).save(any());
    }
}