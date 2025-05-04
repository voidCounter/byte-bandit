package com.bytebandit.fileservice.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import lib.core.enums.UserAction;
import lib.core.events.UserEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserEventConsumerTest {
    
    @Mock
    private UserEventHandlers userEventHandlers;
    
    private UserEventConsumer userEventConsumer;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userEventConsumer = new UserEventConsumer(userEventHandlers);
    }
    
    /**
     * Test to verify that the consumeUserEvent method calls createUserSnapshot when the action is
     * USER_VERIFIED.
     */
    @Test
    void consumeUserEvent_UserVerified_CallsCreateUserSnapshot() {
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        UserEvent userEvent = UserEvent.builder()
            .userId(userId)
            .email(email)
            .action(UserAction.USER_VERIFIED)
            .build();
        
        userEventConsumer.consumeUserEvent(userEvent, userId.toString());
        
        verify(userEventHandlers, times(1)).createUserSnapshot(userId, email);
    }
    
    /**
     * Test to verify that the consumeUserEvent method does not call createUserSnapshot when the
     * action is not USER_VERIFIED.
     *
     * @param userAction The user action to test, which should not be USER_VERIFIED.
     */
    @ParameterizedTest
    @EnumSource(value = UserAction.class, names = "USER_VERIFIED", mode = EnumSource.Mode.EXCLUDE)
    void consumeUserEvent_NotUserVerified_DoesNotCallCreateUserSnapshot(UserAction userAction) {
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        UserEvent userEvent = UserEvent.builder()
            .userId(userId)
            .email(email)
            .action(userAction) // non-USER_VERIFIED action
            .build();
        
        userEventConsumer.consumeUserEvent(userEvent, userId.toString());
        
        verify(userEventHandlers, never()).createUserSnapshot(any(), any());
    }
}