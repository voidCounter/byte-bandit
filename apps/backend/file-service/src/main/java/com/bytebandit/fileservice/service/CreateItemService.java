package com.bytebandit.fileservice.service;

import com.bytebandit.fileservice.dto.CreateItemRequest;
import com.bytebandit.fileservice.dto.CreateItemResponse;
import com.bytebandit.fileservice.enums.FileSystemItemType;
import com.bytebandit.fileservice.enums.UploadStatus;
import com.bytebandit.fileservice.exception.ItemNotFoundException;
import com.bytebandit.fileservice.exception.NotEnoughPermissionException;
import com.bytebandit.fileservice.exception.UserNotFoundException;
import com.bytebandit.fileservice.mapper.FileSystemItemsMapper;
import com.bytebandit.fileservice.model.FileSystemItemEntity;
import com.bytebandit.fileservice.repository.FileSystemItemRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateItemService {

    private final FileSystemItemRepository fileSystemItemRepository;
    private final FileSystemItemsMapper fileSystemItemsMapper;

    /**
     * Creates a new item in the file system.
     *
     * @param createItemRequest the request containing item details
     * @return the response containing created item details
     */
    public CreateItemResponse createItem(CreateItemRequest createItemRequest) {
        UUID parentId = UUID.fromString(createItemRequest.getParentId());
        UUID ownerId = createItemRequest.getOwnerId();

        FileSystemItemEntity parent = getParentItem(parentId);
        validateUserPermission(parentId, ownerId);

        FileSystemItemEntity newItem = buildFileSystemItem(createItemRequest, parent);
        return fileSystemItemsMapper.toCreateItemResponse(fileSystemItemRepository.save(newItem));
    }

    private FileSystemItemEntity getParentItem(UUID parentId) {
        return fileSystemItemRepository.findById(parentId)
            .orElseThrow(() -> new ItemNotFoundException("Parent item not found."));
    }

    private void validateUserPermission(UUID parentId, UUID ownerId) {
        String permission = fileSystemItemRepository.getPermissionRecursive(parentId, ownerId)
            .toLowerCase();
        if ("no_user_found".equals(permission)) {
            throw new UserNotFoundException("User not found.");
        }
        if (!("owner".equals(permission) || "editor".equals(permission))) {
            throw new NotEnoughPermissionException(
                "You do not have enough permission to create this item."
            );
        }
    }

    private FileSystemItemEntity buildFileSystemItem(
        CreateItemRequest request, FileSystemItemEntity parent) {
        return FileSystemItemEntity.builder()
            .chunks(request.getChunks())
            .mimeType(request.getMimeType())
            .name(request.getName())
            .owner(request.getOwnerId())
            .s3Url(request.getS3Url())
            .status(UploadStatus.valueOf(request.getStatus()))
            .type(FileSystemItemType.valueOf(request.getType()))
            .size(request.getSize())
            .parent(parent)
            .build();
    }
}
