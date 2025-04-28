package com.bytebandit.fileservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bytebandit.fileservice.repository.UserSnapshotRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserEventHandlersTest {
    
    @Mock
    private UserSnapshotRepository userSnapshotRepository;
    
    private UserEventHandlers userEventHandlers;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userEventHandlers = new UserEventHandlers(userSnapshotRepository);
    }
    
    /**
     * Test to verify that the createUserSnapshot method creates a snapshot when the user does not
     * exist in the repository.
     */
    @Test
    void createUserSnapshot_UserDoesNotExist_CreatesSnapshot() {
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        when(userSnapshotRepository.existsById(userId)).thenReturn(false);
        
        userEventHandlers.createUserSnapshot(userId, email);
        
        verify(userSnapshotRepository, times(1)).save(argThat(snapshot ->
            snapshot.getUserId().equals(userId) && snapshot.getEmail().equals(email)
        ));
    }
    
    /**
     * Test to verify that the createUserSnapshot method does not create a snapshot when the user
     * exists in the repository.
     */
    @Test
    void createUserSnapshot_UserExists_DoesNotCreateSnapshot() {
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        when(userSnapshotRepository.existsById(userId)).thenReturn(true);
        
        userEventHandlers.createUserSnapshot(userId, email);
        
        verify(userSnapshotRepository, never()).save(any());
    }
    
    /**
     * Test to verify that the createUserSnapshot method throws a RuntimeException when an exception
     * is thrown while checking if the user exists in the repository.
     */
    @Test
    void createUserSnapshot_ExceptionThrown_ThrowsRuntimeException() {
        UUID userId = UUID.randomUUID();
        String email = "test@example.com";
        when(userSnapshotRepository.existsById(userId)).thenThrow(
            new RuntimeException("Database error"));
        
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            userEventHandlers.createUserSnapshot(userId, email)
        );
        assertEquals("Failed to create user snapshot", exception.getMessage());
    }
}