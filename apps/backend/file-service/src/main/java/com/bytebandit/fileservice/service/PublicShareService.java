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
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PublicShareService {
    private final PasswordEncoder passwordEncoder;
    private final SharedItemsPublicRepository sharedItemsPublicRepository;
    private final RoleBasedAccessControlService roleBasedAccessControlService;
    
    /**
     * Constructor for PublicShareService.
     *
     * @param passwordEncoder               PasswordEncoder
     * @param sharedItemsPublicRepository   SharedItemsPublicRepository
     * @param roleBasedAccessControlService RoleBasedAccessControlService
     */
    public PublicShareService(PasswordEncoder passwordEncoder,
                              SharedItemsPublicRepository sharedItemsPublicRepository,
                              RoleBasedAccessControlService roleBasedAccessControlService) {
        this.passwordEncoder = passwordEncoder;
        this.sharedItemsPublicRepository = sharedItemsPublicRepository;
        this.roleBasedAccessControlService = roleBasedAccessControlService;
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
            // Get user's permission on the item
            String usersPermission = roleBasedAccessControlService.getPermission(
                request.getItemId().toString(),
                request.getSharedBy()
            );
            
            // Check if user is authorized to share
            if (!List.of("OWNER", "EDITOR").contains(usersPermission)) {
                throw new PublicShareException("USER IS NOT AUTHORIZED TO SHARE THIS ITEM.");
            }
            
            List<Object[]> results = sharedItemsPublicRepository.callShareItemPublic(
                request.getSharedBy(),
                request.getItemId(),
                request.getPermission().getPermission().toUpperCase(),
                usersPermission,
                request.getPassword() != null ? passwordEncoder.encode(request.getPassword()) :
                    null,
                request.getExpiresAt()
            );
            
            Object[] result = results.get(0);
            UUID publicLinkId = (UUID) result[0];
            String status = (String) result[1];
            
            if (publicLinkId == null) {
                throw new PublicShareException(status);
            }
            
            PublicShareResponse response =
                PublicShareResponse.builder().link(publicLinkId.toString())
                    .permission(request.getPermission().name()).build();
            
            return ResponseEntity.ok(
                ApiResponse.<PublicShareResponse>builder().data(response).status(HttpStatus.SC_OK)
                    .message(status).path("/share/public").build());
        } catch (PublicShareException e) {
            throw e;
        } catch (Exception e) {
            throw new PublicShareException("Failed to share item: " + e.getMessage());
        }
    }
}