package com.bytebandit.fileservice.service;

import com.bytebandit.fileservice.dto.MoveItemRequest;
import com.bytebandit.fileservice.dto.UpdateItemRequest;
import com.bytebandit.fileservice.exception.ItemNotFoundException;
import com.bytebandit.fileservice.exception.MoveItemFailedException;
import com.bytebandit.fileservice.exception.NotEnoughPermissionException;
import com.bytebandit.fileservice.model.FileSystemItemEntity;
import com.bytebandit.fileservice.repository.FileSystemItemRepository;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateItemService {

    private final FileSystemItemRepository fileSystemItemRepository;
    private final RoleBasedAccessControlService roleBasedAccessControlService;

    /**
     * Updates the item with the given ID.
     *
     * @param request the update item request
     * @param userId the ID of the user making the request
     * @return a message indicating the result of the operation
     * @throws NotEnoughPermissionException if the user does not have permission to update the item
     */
    public String updateItem(
        UpdateItemRequest request,
        String userId
    ) {
        if (checkPermission(request.getItemId(), userId)) {
            FileSystemItemEntity item = fileSystemItemRepository.findById(
                convertToUuid(request.getItemId())
            ).orElseThrow(() -> new ItemNotFoundException("Item not found"));

            item.setName(request.getName());
            fileSystemItemRepository.save(item);
            return "Item updated successfully";
        }
        throw new NotEnoughPermissionException("You do not have permission to update this item");
    }

    /**
     * Moves the item with the given ID to the specified parent ID.
     *
     * @param request the move item request
     * @param userId the ID of the user making the request
     * @return a message indicating the result of the operation
     * @throws MoveItemFailedException if the user does not have permission to move the item
     */
    public String moveItem(MoveItemRequest request, String userId) {
        if (checkPermission(request.getItemId(), userId) && checkPermission(request.getParentId(),
            userId)) {
            FileSystemItemEntity itemThatWillBeMoved = fileSystemItemRepository.findById(
                convertToUuid(request.getItemId())
            ).orElseThrow(() -> new ItemNotFoundException("Item not found"));

            FileSystemItemEntity parentItem = fileSystemItemRepository.findById(
                convertToUuid(request.getParentId())
            ).orElseThrow(() -> new ItemNotFoundException("Parent item not found"));

            if (itemThatWillBeMoved.getParent().getId().equals(
                convertToUuid(request.getParentId()))
            ) {
                throw new MoveItemFailedException("Item is already in the requested parent");
            }

            itemThatWillBeMoved.setParent(parentItem);
            fileSystemItemRepository.save(itemThatWillBeMoved);
        }
        throw new MoveItemFailedException("You do not have permission to move this item");
    }

    private boolean checkPermission(String itemId, String userId) {
        final String permission = roleBasedAccessControlService.getPermission(
            itemId,
            convertToUuid(userId)
        ).toUpperCase();

        return permission.equals("OWNER") || permission.equals("EDITOR");
    }

    private UUID convertToUuid(String id) {
        return UUID.fromString(id);
    }
}
