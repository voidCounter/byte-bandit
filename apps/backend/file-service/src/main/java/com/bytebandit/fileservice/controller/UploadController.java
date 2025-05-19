package com.bytebandit.fileservice.controller;

import com.bytebandit.fileservice.dto.FileNameRequest;
import com.bytebandit.fileservice.service.S3FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lib.core.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/upload")
@Tag(
    name = "Upload",
    description = "File upload operations"
)
public class UploadController {
    private final S3FileService s3FileService;
    
    public UploadController(S3FileService s3FileService) {
        this.s3FileService = s3FileService;
    }

    @Operation(
        summary = "Generate presigned URL for file upload",
        description = "Generates a presigned URL for uploading a file to S3."
    )
    @PostMapping("/presigned-url")
    ResponseEntity<ApiResponse<String>> generateUploadPresignedUrl(
        @RequestBody FileNameRequest fileNameRequest) {
        return ResponseEntity.ok(
            s3FileService.generateUploadPresignedUrl(fileNameRequest.getFileName()));
    }
}
