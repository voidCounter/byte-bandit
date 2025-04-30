package com.bytebandit.fileservice.service;

import com.bytebandit.fileservice.enums.FileSystemItemType;
import com.bytebandit.fileservice.enums.UploadStatus;
import com.bytebandit.fileservice.exception.DataAccessException;
import com.bytebandit.fileservice.model.FileSystemItemEntity;
import com.bytebandit.fileservice.model.UserSnapshotEntity;
import com.bytebandit.fileservice.repository.FileSystemItemRepository;
import com.bytebandit.fileservice.repository.UserSnapshotRepository;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserEventHandlers {
    
    private final UserSnapshotRepository userSnapshotRepository;
    
    private static final Logger logger = LoggerFactory.getLogger(UserEventHandlers.class);
    private final FileSystemItemRepository fileSystemItemRepository;
    
    public UserEventHandlers(UserSnapshotRepository userSnapshotRepository,
                             FileSystemItemRepository fileSystemItemRepository) {
        this.userSnapshotRepository = userSnapshotRepository;
        this.fileSystemItemRepository = fileSystemItemRepository;
    }
    
    /**
     * Creates a user snapshot if it does not already exist.
     *
     * @param userId userId of the user
     * @param email  email of the user
     */
    @Transactional
    protected void createUserSnapshot(UUID userId, String email) {
        try {
            if (!userSnapshotRepository.existsById(userId)) {
                UserSnapshotEntity userSnapshot =
                    UserSnapshotEntity.builder().userId(userId).email(email).build();
                
                userSnapshotRepository.save(userSnapshot);
                if (!fileSystemItemRepository.existsByOwnerAndParentIsNull(userId)) {
                    fileSystemItemRepository.save(
                        FileSystemItemEntity.builder()
                            .name("home")
                            .owner(userId)
                            .type(FileSystemItemType.FOLDER)
                            .status(UploadStatus.UPLOADED).build());
                }
                logger.debug("Created user snapshot: userId={}, email={}", userId, email);
            } else {
                logger.debug("User snapshot already exists: userId={}", userId);
            }
        } catch (Exception e) {
            throw new DataAccessException("Failed to create user snapshot") {
            };
        }
    }
}
