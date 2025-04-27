package com.bytebandit.fileservice.service;

import com.bytebandit.fileservice.dto.PublicShareRequest;
import com.bytebandit.fileservice.dto.PublicShareResponse;
import com.bytebandit.fileservice.exception.PublicShareException;
import com.bytebandit.fileservice.repository.SharedItemsPublicRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lib.core.dto.response.ApiResponse;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PublicShareService {
    private static final Logger logger = LoggerFactory.getLogger(PublicShareService.class);
    private final PasswordEncoder passwordEncoder;
    private final SharedItemsPublicRepository sharedItemsPublicRepository;
    
    public PublicShareService(PasswordEncoder passwordEncoder,
                              SharedItemsPublicRepository sharedItemsPublicRepository) {
        this.passwordEncoder = passwordEncoder;
        this.sharedItemsPublicRepository = sharedItemsPublicRepository;
    }
    
    /**
     * Method to share an item publicly.
     *
     * @param request DTO of the request
     *
     * @return ResponseEntity with ApiResponse containing the public share response.
     */
    @Transactional
    public ResponseEntity<ApiResponse<PublicShareResponse>> sharePublic(
        PublicShareRequest request) {
        try {
            List<Object[]> results = sharedItemsPublicRepository.callShareItemPublic(
                request.getSharedBy(),
                request.getItemId(),
                request.getPermission().getPermission().toUpperCase(),
                request.getPassword() != null ? passwordEncoder.encode(request.getPassword()) : null
            );
            
            logger.debug("Function executed successfully");
            
            // Process the result set
            if (results.isEmpty()) {
                throw new PublicShareException("No results returned from function");
            }
            
            Object[] result = results.get(0);
            UUID publicLinkId = result[0] != null ? (UUID) result[0] : null;
            String errorMessage = result[1] != null ? (String) result[1] : null;
            
            if (errorMessage != null && !errorMessage.isEmpty()) {
                logger.warn("Error sharing item: {}", errorMessage);
                throw new PublicShareException(errorMessage);
            }
            
            PublicShareResponse responseDto = PublicShareResponse.builder()
                .link(publicLinkId != null ? publicLinkId.toString() : null)
                .permission(request.getPermission().toString())
                .build();
            
            return ResponseEntity.ok().body(
                ApiResponse.<PublicShareResponse>builder()
                    .status(HttpStatus.SC_OK)
                    .message("Item shared successfully")
                    .data(responseDto)
                    .build());
        } catch (PublicShareException e) {
            throw e;
        } catch (Exception e) {
            throw new PublicShareException("Unexpected error: " + e.getMessage());
        }
    }
}