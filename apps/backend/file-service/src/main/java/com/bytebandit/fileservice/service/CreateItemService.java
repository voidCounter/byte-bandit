package com.bytebandit.fileservice.service;

import com.bytebandit.fileservice.dto.CreateItemRequest;
import com.bytebandit.fileservice.dto.CreateItemResponse;
import com.bytebandit.fileservice.enums.FileSystemItemType;
import com.bytebandit.fileservice.enums.UploadStatus;
import com.bytebandit.fileservice.exception.ItemNotFoundException;
import com.bytebandit.fileservice.exception.NotEnoughPermissionException;
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
     * Creates a new file system item.
     */
    public CreateItemResponse createItem(
        CreateItemRequest createItemRequest
    ) {

        final FileSystemItemEntity parent = fileSystemItemRepository.findById(
            UUID.fromString(createItemRequest.getParentId())
        ).orElseThrow(() -> new ItemNotFoundException(
            "Parent item not found."
        ));

        final String permission
            = fileSystemItemRepository.getPermissionRecursive(
            UUID.fromString(createItemRequest.getParentId()),
            createItemRequest.getOwnerId()
        ).toLowerCase();

        if (!(permission.equals("owner") || permission.equals("editor"))) {
            throw new NotEnoughPermissionException(
                "You do not have enough permission to create this item."
            );
        }

        FileSystemItemEntity fileSystemItemEntity = FileSystemItemEntity.builder()
            .chunks(createItemRequest.getChunks())
            .mimeType(createItemRequest.getMimeType())
            .name(createItemRequest.getName())
            .owner(createItemRequest.getOwnerId())
            .s3Url(createItemRequest.getS3Url())
            .status(UploadStatus.valueOf(createItemRequest.getStatus()))
            .type(FileSystemItemType.valueOf(createItemRequest.getType()))
            .size(createItemRequest.getSize())
            .parent(parent)
            .build();

        return fileSystemItemsMapper.toCreateItemResponse(
            fileSystemItemRepository.save(fileSystemItemEntity)
        );
    }
}
