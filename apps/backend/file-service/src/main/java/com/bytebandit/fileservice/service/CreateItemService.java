package com.bytebandit.fileservice.service;

import com.bytebandit.fileservice.dto.CreateItemRequest;
import com.bytebandit.fileservice.dto.CreateItemResponse;
import com.bytebandit.fileservice.repository.FileSystemItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateItemService {

    private final FileSystemItemRepository fileSystemItemRepository;

    /**
     * Creates a new file system item.
     */
    public CreateItemResponse createItem(
        CreateItemRequest createItemRequest
    ) {
        return null; // to do
    }
}
