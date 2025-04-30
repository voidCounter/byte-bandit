package com.bytebandit.fileservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3Config {
    @Value("${aws.region}")
    private String awsRegion;
    
    /**
     * Configures and provides a bean of type S3Presigner that can be used to generate presigned
     * URLs for accessing Amazon S3 objects.
     *
     * @return an instance of S3Presigner configured with the specified AWS region and default
     *     credentials provider.
     */
    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
            .region(Region.of(awsRegion))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();
    }
}
