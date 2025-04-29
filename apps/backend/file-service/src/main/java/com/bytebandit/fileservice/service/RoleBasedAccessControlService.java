package com.bytebandit.fileservice.service;

import com.bytebandit.fileservice.exception.ItemPasswordVerificationFailedException;
import com.bytebandit.fileservice.exception.ItemProtectedWithPasswordException;
import com.bytebandit.fileservice.model.SharedItemsPublicEntity;
import com.bytebandit.fileservice.repository.FileSystemItemRepository;
import com.bytebandit.fileservice.repository.SharedItemsPublicRepository;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleBasedAccessControlService {

    private final FileSystemItemRepository fileSystemItemRepository;
    private final SharedItemsPublicRepository sharedItemsPublicRepository;
    private final PasswordEncoder passwordEncoder;

    public String getPermission(String itemId, UUID userId) {
        return fileSystemItemRepository.getPermissionRecursive(
            UUID.fromString(itemId), userId);
    }

    /**
     * Check if an item is password protected.
     */
    public boolean isPasswordProtected(String itemId) {
        return sharedItemsPublicRepository.findByItemId(
                UUID.fromString(itemId)
            ).getPasswordHash() != null;
    }

    /**
     * Check if the provided password is correct.
     */
    public void validatePassword(UUID itemId, String password) {
        if (password == null) {
            throw new ItemProtectedWithPasswordException("The item you are trying to access is "
                + "protected by  password");
        }
        SharedItemsPublicEntity publicShareItems =
            sharedItemsPublicRepository.findByItemIdAndExpiresAtIsBefore(
                itemId, Timestamp.from(Instant.now())
            );
        if (!passwordEncoder.matches(password, publicShareItems.getPasswordHash())) {
            throw new ItemPasswordVerificationFailedException("Provided password is wrong");
        }
    }
}
