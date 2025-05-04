package com.bytebandit.fileservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.bytebandit.fileservice.dto.FileNameRequest;
import com.bytebandit.fileservice.exception.InvalidFileNameException;
import com.bytebandit.fileservice.service.S3FileService;
import java.time.Instant;
import lib.core.dto.response.ApiResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class UploadControllerTest {
    
    @Mock
    private S3FileService s3FileService;
    
    @InjectMocks
    private UploadController uploadController;
    
    private FileNameRequest fileNameRequest;
    private ApiResponse<String> successResponse;
    
    /**
     * Sets up the test environment before each test case.
     */
    @BeforeEach
    void setUp() {
        fileNameRequest = new FileNameRequest();
        successResponse = ApiResponse.<String>builder()
            .status(200)
            .message("Pre-signed URL generated successfully")
            .data("https://s3.amazonaws.com/bucket/test-file")
            .timestamp(Instant.now().toString())
            .path("/api/v1/files/upload/presigned-url")
            .build();
    }
    
    /**
     * Tests the generateUploadPresignedUrl method for a successful case.
     */
    @Test
    void testGeneratePresignedUrl_Success() {
        fileNameRequest.setFileName("test-file.txt");
        when(s3FileService.generateUploadPresignedUrl("test-file.txt")).thenReturn(successResponse);
        
        ResponseEntity<ApiResponse<String>> response =
            uploadController.generateUploadPresignedUrl(fileNameRequest);
        
        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
        assertEquals(successResponse, response.getBody());
        Assertions.assertNotNull(response.getBody());
        assertEquals("https://s3.amazonaws.com/bucket/test-file", response.getBody().getData());
    }
    
    /**
     * Tests the generateUploadPresignedUrl method for an invalid file name.
     */
    @Test
    void testGeneratePresignedUrl_EmptyFileName() {
        fileNameRequest.setFileName("");
        when(s3FileService.generateUploadPresignedUrl(""))
            .thenThrow(new InvalidFileNameException("File name cannot be empty"));
        
        try {
            uploadController.generateUploadPresignedUrl(fileNameRequest);
        } catch (InvalidFileNameException ex) {
            assertEquals("File name cannot be empty", ex.getMessage());
        }
    }
    
    /**
     * Tests the generateUploadPresignedUrl method for a null file name.
     */
    @Test
    void testGeneratePresignedUrl_NullFileName() {
        fileNameRequest.setFileName(null);
        when(s3FileService.generateUploadPresignedUrl(null))
            .thenThrow(new InvalidFileNameException("File name cannot be empty"));
        
        try {
            uploadController.generateUploadPresignedUrl(fileNameRequest);
        } catch (InvalidFileNameException ex) {
            assertEquals("File name cannot be empty", ex.getMessage());
        }
    }
}