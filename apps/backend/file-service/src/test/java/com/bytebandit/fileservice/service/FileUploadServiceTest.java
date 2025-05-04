package com.bytebandit.fileservice.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.bytebandit.fileservice.exception.InvalidFileNameException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.http.SdkHttpFullRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

@ExtendWith(MockitoExtension.class)
class FileUploadServiceTest {

    @InjectMocks
    private S3FileService fileUploadService;

    @MockitoBean
    private S3Presigner s3Presigner;

    @MockitoBean
    private PresignedPutObjectRequest presignedRequest;

    @MockitoBean
    private SdkHttpFullRequest sdkHttpRequest;


    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fileUploadService, "bucketName", "test-bucket");
    }


    /**
     * Test case to verify that an exception is thrown when the file name is null.
     * This test checks the behavior of the generateUploadPresignedUrl method when
     * provided with a null file name.
     */
    @Test
    void shouldThrowExceptionWhenFileNameIsNull() {
        assertThatThrownBy(() -> fileUploadService.generateUploadPresignedUrl(null))
            .isInstanceOf(InvalidFileNameException.class)
            .hasMessageContaining("File name cannot be empty");
    }

    /**
     * Test case to verify that an exception is thrown when the file name is empty or
     * contains only whitespace characters. This test checks the behavior of the
     * generateUploadPresignedUrl method when provided with an empty or whitespace-only
     * file name.
     */
    @Test
    void shouldThrowExceptionWhenFileNameIsEmptyOrWhitespace() {
        List<String> inputs = Arrays.asList("", "   ", "\n\t");

        for (String input : inputs) {
            assertThatThrownBy(() -> fileUploadService.generateUploadPresignedUrl(input))
                .isInstanceOf(InvalidFileNameException.class)
                .hasMessageContaining("File name cannot be empty");
        }
    }
}