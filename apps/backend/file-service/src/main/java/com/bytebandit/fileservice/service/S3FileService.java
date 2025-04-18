package com.bytebandit.fileservice.service;

import java.time.Instant;
import lib.core.dto.response.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
public class S3FileService {
    private final S3Presigner s3Presigner;
    private static final org.slf4j.Logger logger =
        org.slf4j.LoggerFactory.getLogger(S3FileService.class);
    
    
    @Value("${aws.s3.bucket}")
    private String bucketName;
    
    public S3FileService(S3Presigner s3Presigner) {
        this.s3Presigner = s3Presigner;
    }
    
    /**
     * Generates a pre-signed URL for uploading a file to an S3 bucket. The generated URL allows
     * temporary access to upload the specified file with appropriate HTTP PUT permissions. The URL
     * is valid for 10 minutes.
     *
     * @param fileName the name of the file to be uploaded. The file name should not be null or
     *                 empty; invalid file names will result in a 400 status response.
     *
     * @return an ApiResponse object encapsulating the status, message, and the generated pre-signed
     *     URL along with other metadata. If the file name is invalid, the response will include an
     *     appropriate error message and a 400 status code.
     */
    public ApiResponse<String> generateUploadPresignedUrl(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return ApiResponse.<String>builder()
                .status(400)
                .message("File name cannot be empty")
                .timestamp(Instant.now().toString())
                .path("/api/files/presigned-upload")
                .build();
        }
        
        // sanitize file name
        String sanitizedFileName =
            Instant.now().toEpochMilli() + "-" + fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(sanitizedFileName)
            .build();
        
        PutObjectPresignRequest presignRequest =
            PutObjectPresignRequest.builder()
                .signatureDuration(java.time.Duration.ofMinutes(10))
                .putObjectRequest(putObjectRequest)
                .build();
        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        String presignedUrl = presignedRequest.url().toString();
        logger.info("Presigned URL to upload a file to: [{}]", presignedUrl);
        logger.info("HTTP method: [{}]", presignedRequest.httpRequest().method());
        return ApiResponse.<String>builder()
            .status(200)
            .message("Pre-signed URL generated successfully")
            .data(presignedUrl)
            .timestamp(Instant.now().toString())
            .path("/api/files/presigned-upload")
            .build();
    }
    
}
