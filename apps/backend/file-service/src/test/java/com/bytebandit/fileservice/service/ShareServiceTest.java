package com.bytebandit.fileservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bytebandit.fileservice.dto.PublicShareRequest;
import com.bytebandit.fileservice.dto.PublicShareResponse;
import com.bytebandit.fileservice.enums.FileSystemPermission;
import com.bytebandit.fileservice.exception.PublicShareException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.StoredProcedureQuery;
import java.util.UUID;
import lib.core.dto.response.ApiResponse;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

class ShareServiceTest {
    
    @Mock
    private EntityManager entityManager;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private StoredProcedureQuery storedProcedureQuery;
    
    @InjectMocks
    private ShareService shareService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    private static final UUID OWNER_ID = UUID.randomUUID();
    private static final UUID ITEM_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    
    @Test
    void testSharePublic_shareSuccessfully() {
        // Arrange
        PublicShareRequest request =
            new PublicShareRequest(OWNER_ID, ITEM_ID,
                FileSystemPermission.VIEWER, null);
        UUID publicLinkId = UUID.randomUUID();
        
        when(entityManager.createStoredProcedureQuery("share_item_public")).thenReturn(
            storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), any(),
            any())).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.setParameter(anyString(), any())).thenReturn(
            storedProcedureQuery);
        when(storedProcedureQuery.getOutputParameterValue("p_public_link_id")).thenReturn(
            publicLinkId);
        when(storedProcedureQuery.getOutputParameterValue("p_error_message")).thenReturn(null);
        
        // Act
        ResponseEntity<ApiResponse<PublicShareResponse>> response =
            shareService.sharePublic(request);
        
        // Assert
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.SC_OK, response.getBody().getStatus());
        assertEquals("Item shared successfully", response.getBody().getMessage());
        assertEquals(publicLinkId.toString(), response.getBody().getData().getLink());
        verify(storedProcedureQuery).execute();
    }
    
    /**
     * Test case for validation error during public sharing.
     */
    @Test
    void testSharePublic_ValidationError() {
        // Arrange
        PublicShareRequest request =
            new PublicShareRequest(OWNER_ID, USER_ID,
                FileSystemPermission.EDITOR, null);
        String errorMessage = "User does not exist.";
        
        when(entityManager.createStoredProcedureQuery("share_item_public")).thenReturn(
            storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), any(),
            any())).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.setParameter(anyString(), any())).thenReturn(
            storedProcedureQuery);
        when(storedProcedureQuery.getOutputParameterValue("p_error_message")).thenReturn(
            errorMessage);
        
        // Act & Assert
        PublicShareException exception =
            assertThrows(PublicShareException.class, () -> shareService.sharePublic(request));
        assertEquals(errorMessage, exception.getMessage());
        verify(storedProcedureQuery).execute();
    }
    
    /**
     * Test case for PersistenceException during public sharing.
     */
    @Test
    void testSharePublic_PersistenceException() {
        // Arrange
        PublicShareRequest request =
            new PublicShareRequest(OWNER_ID, USER_ID,
                FileSystemPermission.VIEWER, null);
        
        when(entityManager.createStoredProcedureQuery("share_item_public")).thenReturn(
            storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), any(),
            any())).thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.setParameter(anyString(), any())).thenReturn(
            storedProcedureQuery);
        doThrow(new RuntimeException("Database error")).when(storedProcedureQuery).execute();
        
        // Act & Assert
        PublicShareException exception =
            assertThrows(PublicShareException.class, () -> shareService.sharePublic(request));
        assertTrue(exception.getMessage().contains("Error sharing item"));
    }
    
    /**
     * Test case for unexpected exceptions during public sharing.
     */
    @Test
    void testSharePublic_UnexpectedException() {
        // Arrange
        PublicShareRequest request =
            new PublicShareRequest(OWNER_ID, USER_ID,
                FileSystemPermission.VIEWER, null);
        
        when(entityManager.createStoredProcedureQuery("share_item_public")).thenThrow(
            new RuntimeException("Unexpected error"));
        
        // Act & Assert
        PublicShareException exception =
            assertThrows(PublicShareException.class, () -> shareService.sharePublic(request));
        assertTrue(exception.getMessage().contains("Unexpected error"));
    }
}
