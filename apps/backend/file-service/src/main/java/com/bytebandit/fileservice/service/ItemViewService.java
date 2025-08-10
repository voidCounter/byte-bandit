package com.bytebandit.fileservice.service;

import com.bytebandit.fileservice.dto.ItemViewRequest;
import com.bytebandit.fileservice.dto.ItemViewResponse;
import com.bytebandit.fileservice.exception.InvalidFileNameException;
import com.bytebandit.fileservice.exception.ItemViewException;
import com.bytebandit.fileservice.mapper.ItemViewMapper;
import com.bytebandit.fileservice.projection.ItemViewProjection;
import com.bytebandit.fileservice.repository.FileSystemItemRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemViewService {
    
    private final FileSystemItemRepository fileSystemItemRepository;
    private final RoleBasedAccessControlService roleBasedAccessControlService;
    
    /**
     * Retrieves the view of an item based on the provided request, user ID, and permission.
     *
     * @param itemViewRequest The request containing the item ID and optional password.
     * @param userId          The ID of the user making the request.
     *
     * @return An ItemViewResponse containing the details of the item.
     * @throws ItemViewException if the item is not found or if there are any issues retrieving it.
     */
    public ItemViewResponse getItemView(
        ItemViewRequest itemViewRequest,
        UUID userId
    ) {
        final String permission = roleBasedAccessControlService.getPermission(
            itemViewRequest.getItemId(),
            userId
        ).toUpperCase();
        
        log.info("permission: {}", permission);
        
        if (permission.equals("NO_ACCESS")) {
            throw new ItemViewException("You do not have access to this item.");
        }
        
        boolean isAccessible = permission.equals("OWNER")
            || !roleBasedAccessControlService.isPasswordProtected(itemViewRequest.getItemId());
        
        log.info("protected? {}", isAccessible);
        
        if (!isAccessible) {
            roleBasedAccessControlService.validatePassword(
                UUID.fromString(itemViewRequest.getItemId()),
                itemViewRequest.getPassword()
            );
        }
        
        try {
            ItemViewProjection response = fileSystemItemRepository.viewItems(
                UUID.fromString(itemViewRequest.getItemId()),
                userId,
                permission
            );
            log.info("Item view response childs: {}", response.getOutputItemId());
            return ItemViewMapper.mapToResponse(response);
        } catch (DataAccessException e) {
            throw new ItemViewException("Error accessing item with id: {}"
                + itemViewRequest.getItemId());
        }
    }
    
    
    /**
     * Retrieves all items of a user.
     *
     * @param userId The ID of the user.
     *
     * @return An ItemViewResponse containing the details of the user's items.
     */
    public ItemViewResponse getUserItems(String userId) {
        try {
            return ItemViewMapper.mapToResponse(
                fileSystemItemRepository.userItems(UUID.fromString(userId))
            );
        } catch (DataAccessException e) {
            throw new ItemViewException("Error accessing user items");
        }
    }
    
    /**
     * Retrieves all items shared with a user.
     *
     * @param userId The ID of the user.
     *
     * @return A list of ItemViewResponse containing the details of the shared items.
     */
    public List<ItemViewResponse> getSharedWithMe(String userId) {
        try {
            List<ItemViewProjection> projections = fileSystemItemRepository.sharedWithUser(
                UUID.fromString(userId));
            return projections.stream()
                .map(ItemViewMapper::mapToResponse)
                .toList();
        } catch (DataAccessException e) {
            throw new ItemViewException("Error accessing shared items");
        }
    }
    
    /**
     * Retrieves the parent directory of a user's home directory.
     *
     * @param userId The ID of the user.
     *
     * @return The ID of the user's home parent directory.
     */
    public String getUserHomeParent(String userId) {
        return fileSystemItemRepository.findByOwnerAndParentIsNull(
            UUID.fromString(userId)).orElseThrow().getId().toString();
    }
}
