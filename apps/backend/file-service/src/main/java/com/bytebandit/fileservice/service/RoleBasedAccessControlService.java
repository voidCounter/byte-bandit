package com.bytebandit.fileservice.service;

import com.bytebandit.fileservice.repository.FileSystemItemRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleBasedAccessControlService {

    private final FileSystemItemRepository fileSystemItemRepository;

    public String getPermission(String itemId, UUID userId) {
        return fileSystemItemRepository.getPermissionRecursive(
            UUID.fromString(itemId), userId);
    }
}
