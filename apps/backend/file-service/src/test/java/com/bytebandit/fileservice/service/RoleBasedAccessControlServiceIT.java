package com.bytebandit.fileservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.bytebandit.fileservice.configurer.AbstractPostgresContainer;
import com.bytebandit.fileservice.enums.FileSystemItemType;
import com.bytebandit.fileservice.enums.FileSystemPermission;
import com.bytebandit.fileservice.enums.UploadStatus;
import com.bytebandit.fileservice.exception.ItemPasswordVerificationFailedException;
import com.bytebandit.fileservice.exception.ItemProtectedWithPasswordException;
import com.bytebandit.fileservice.model.FileSystemItemEntity;
import com.bytebandit.fileservice.model.SharedItemsPrivateEntity;
import com.bytebandit.fileservice.model.SharedItemsPublicEntity;
import com.bytebandit.fileservice.model.UserSnapshotEntity;
import com.bytebandit.fileservice.repository.FileSystemItemRepository;
import com.bytebandit.fileservice.repository.SharedItemsPrivateRepository;
import com.bytebandit.fileservice.repository.SharedItemsPublicRepository;
import com.bytebandit.fileservice.repository.UserSnapshotRepository;
import io.restassured.RestAssured;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class RoleBasedAccessControlServiceIT extends AbstractPostgresContainer {

    @LocalServerPort
    private int port;

    @Autowired
    private FileSystemItemRepository fileSystemItemRepository;

    @Autowired
    private SharedItemsPrivateRepository sharedItemsPrivateRepository;

    @Autowired
    private SharedItemsPublicRepository sharedItemsPublicRepository;

    @Autowired
    private RoleBasedAccessControlService permissionService;

    @Autowired
    private UserSnapshotRepository userSnapshotRepository;

    private UUID ownerId;
    private UUID otherUserId;

    /**
     * Constructor for RoleBasedAccessControlServiceIT.
     */
    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        ownerId = UUID.randomUUID();
        otherUserId = UUID.randomUUID();
    }

    /**
     * Test for getPermission method.
     */
    @Test
    void shouldReturnOwnerPermissionWhenUserIsOwner() {
        userSnapshotRepository.save(
            new UserSnapshotEntity(ownerId, "valid-mail-1@domain.com")
        );

        FileSystemItemEntity item = new FileSystemItemEntity();
        item.setName("Test File");
        item.setSize(100L);
        item.setMimeType("text/plain");
        item.setOwner(ownerId);
        item.setStatus(UploadStatus.NOT_UPLOADED);
        item.setType(FileSystemItemType.FILE);
        item.setS3Url("s3://test-bucket/test-file");
        item = fileSystemItemRepository.save(item);


        String permission = permissionService.getPermission(item.getId().toString(), ownerId);

        assertThat(permission).isEqualTo("OWNER");
    }

    /**
     * Test for getPermission method when user is not the owner.
     */
    @Test
    void shouldReturnEditorPermissionWhenUserHasPrivateEditorShare() {
        userSnapshotRepository.save(
            new UserSnapshotEntity(otherUserId, "valid-mail-3@domain.com")
        );

        FileSystemItemEntity item = new FileSystemItemEntity();
        item.setName("Shared File");
        item.setSize(200L);
        item.setMimeType("application/pdf");
        item.setOwner(UUID.randomUUID());
        item.setStatus(UploadStatus.NOT_UPLOADED);
        item.setType(FileSystemItemType.FILE);
        item.setS3Url("s3://test-bucket/shared-file");
        item = fileSystemItemRepository.save(item);

        userSnapshotRepository.save(
            new UserSnapshotEntity(item.getOwner(), "valid-mail-2@domain.com")
        );

        SharedItemsPrivateEntity privateShare = new SharedItemsPrivateEntity();
        privateShare.setItem(item);
        privateShare.setUserId(item.getOwner());
        privateShare.setSharedWith(otherUserId);
        privateShare.setPermission(FileSystemPermission.EDITOR);
        sharedItemsPrivateRepository.save(privateShare);

        String permission = permissionService.getPermission(item.getId().toString(), otherUserId);

        assertThat(permission).isEqualTo("EDITOR");
    }

    /**
     * Test for getPermission method when user has viewer permission.
     */
    @Test
    void shouldReturnViewerPermissionWhenUserHasPrivateViewerShare() {
        FileSystemItemEntity item = new FileSystemItemEntity();
        item.setName("Viewer Shared File");
        item.setSize(300L);
        item.setMimeType("image/jpeg");
        item.setOwner(UUID.randomUUID());
        item.setStatus(UploadStatus.NOT_UPLOADED);
        item.setType(FileSystemItemType.FILE);
        item.setS3Url("s3://test-bucket/viewer-file");
        item = fileSystemItemRepository.save(item);

        userSnapshotRepository.save(
            new UserSnapshotEntity(item.getOwner(), "valid-mail-2@domain.com")
        );

        userSnapshotRepository.save(
            new UserSnapshotEntity(otherUserId, "valid-mail-3@domain.com")
        );

        SharedItemsPrivateEntity privateShare = new SharedItemsPrivateEntity();
        privateShare.setItem(item);
        privateShare.setUserId(item.getOwner());
        privateShare.setSharedWith(otherUserId);
        privateShare.setPermission(FileSystemPermission.VIEWER);
        sharedItemsPrivateRepository.save(privateShare);

        String permission = permissionService.getPermission(item.getId().toString(), otherUserId);

        assertThat(permission).isEqualTo("VIEWER");
    }

    /**
     * Test for getPermission method when user has no permission.
     */
    @Test
    void shouldReturnNoAccessWhenUserHasNoPermission() {
        FileSystemItemEntity item = new FileSystemItemEntity();
        item.setName("Private File");
        item.setSize(400L);
        item.setMimeType("application/zip");
        item.setOwner(UUID.randomUUID());
        item.setStatus(UploadStatus.NOT_UPLOADED);
        item.setType(FileSystemItemType.FILE);
        item.setS3Url("s3://test-bucket/private-file");
        item = fileSystemItemRepository.save(item);

        userSnapshotRepository.save(
            new UserSnapshotEntity(item.getOwner(), "valid-mail-2@domain.com")
        );

        userSnapshotRepository.save(
            new UserSnapshotEntity(otherUserId, "valid-mail-3@domain.com")
        );

        String permission = permissionService.getPermission(item.getId().toString(), otherUserId);

        assertThat(permission).isEqualTo("NO_ACCESS");
    }

    /**
     * Test for getPermission method when user has public share.
     */
    @Test
    void shouldReturnEditorPermissionFromPublicShare() {
        FileSystemItemEntity item = new FileSystemItemEntity();
        item.setName("Public Shared File");
        item.setSize(500L);
        item.setMimeType("application/msword");
        item.setOwner(UUID.randomUUID());
        item.setStatus(UploadStatus.UPLOADED);
        item.setType(FileSystemItemType.FILE);
        item.setS3Url("s3://test-bucket/public-shared-file");
        item = fileSystemItemRepository.save(item);

        userSnapshotRepository.save(
            new UserSnapshotEntity(item.getOwner(), "valid-mail-2@domain.com")
        );

        userSnapshotRepository.save(
            new UserSnapshotEntity(otherUserId, "valid-mail-3@domain.com")
        );

        SharedItemsPublicEntity publicShare = new SharedItemsPublicEntity();
        publicShare.setItem(item);
        publicShare.setPermission(FileSystemPermission.EDITOR);
        publicShare.setSharedBy(item.getOwner());
        publicShare.setPasswordHash("dummyhash");
        publicShare.setExpiresAt(Timestamp.from(Instant.now().plusSeconds(3600)));
        sharedItemsPublicRepository.save(publicShare);

        String permission = permissionService.getPermission(item.getId().toString(), otherUserId);

        assertThat(permission).isEqualTo("EDITOR");
    }

    /**
     * Test for getPermission method when user has public share with viewer permission.
     */
    @Test
    void shouldReturnTrueWhenItemIsPasswordProtected() {
        FileSystemItemEntity item = createAndSaveFileItem();

        SharedItemsPublicEntity publicShare = new SharedItemsPublicEntity();
        publicShare.setItem(item);
        publicShare.setPermission(FileSystemPermission.VIEWER);
        publicShare.setSharedBy(item.getOwner());
        publicShare.setPasswordHash("someHash");
        publicShare.setExpiresAt(Timestamp.from(Instant.now().plusSeconds(3600)));
        sharedItemsPublicRepository.save(publicShare);

        boolean isProtected = permissionService.isPasswordProtected(item.getId().toString());
        assertThat(isProtected).isTrue();
    }

    /**
     * Test for getPermission method when user has public share with no password.
     */
    @Test
    void shouldReturnFalseWhenItemIsNotSharedPublicly() {
        FileSystemItemEntity item = createAndSaveFileItem();
        boolean isProtected = permissionService.isPasswordProtected(item.getId().toString());
        assertThat(isProtected).isFalse();
    }

    /**
     * Test for getPermission method when user has public share with null password.
     */
    @Test
    void shouldReturnFalseWhenPasswordHashIsNull() {
        FileSystemItemEntity item = createAndSaveFileItem();

        SharedItemsPublicEntity publicShare = new SharedItemsPublicEntity();
        publicShare.setItem(item);
        publicShare.setPermission(FileSystemPermission.VIEWER);
        publicShare.setSharedBy(item.getOwner());
        publicShare.setPasswordHash(null);
        publicShare.setExpiresAt(Timestamp.from(Instant.now().plusSeconds(3600)));
        sharedItemsPublicRepository.save(publicShare);

        boolean isProtected = permissionService.isPasswordProtected(item.getId().toString());
        assertThat(isProtected).isFalse();
    }

    /**
     * Test for getPermission method when user has public share with null password.
     */
    @Test
    void shouldThrowExceptionWhenPasswordIsNull() {
        FileSystemItemEntity item = createAndSaveFileItem();

        SharedItemsPublicEntity publicShare = new SharedItemsPublicEntity();
        publicShare.setItem(item);
        publicShare.setPermission(FileSystemPermission.VIEWER);
        publicShare.setSharedBy(item.getOwner());
        publicShare.setPasswordHash("hashedPassword");
        publicShare.setExpiresAt(Timestamp.from(Instant.now().plusSeconds(3600)));
        sharedItemsPublicRepository.save(publicShare);

        assertThatThrownBy(() ->
            permissionService.validatePassword(item.getId(), null)
        ).isInstanceOf(ItemProtectedWithPasswordException.class)
            .hasMessageContaining("protected by  password");
    }

    /**
     * Test for getPermission method when user has public share with null password.
     */
    @Test
    void shouldThrowExceptionWhenPasswordIsInvalid() {
        FileSystemItemEntity item = createAndSaveFileItem();

        String actualPassword = "correct-password";
        String hash = new BCryptPasswordEncoder().encode(actualPassword);

        SharedItemsPublicEntity publicShare = new SharedItemsPublicEntity();
        publicShare.setItem(item);
        publicShare.setPermission(FileSystemPermission.VIEWER);
        publicShare.setSharedBy(item.getOwner());
        publicShare.setPasswordHash(hash);
        publicShare.setExpiresAt(Timestamp.from(Instant.now().plusSeconds(3600)));
        sharedItemsPublicRepository.save(publicShare);

        log.info("item id: {}", item.getId());
        log.info("in public share: {}", publicShare.getItem().getId());

        assertThatThrownBy(() ->
            permissionService.validatePassword(item.getId(), "wrong-password")
        ).isInstanceOf(ItemPasswordVerificationFailedException.class)
            .hasMessageContaining("wrong");
    }

    /**
     * Test for getPermission method when user has public share with null password.
     */
    @Test
    void shouldPassWhenPasswordIsCorrect() {
        FileSystemItemEntity item = createAndSaveFileItem();

        String password = "valid-password";
        String hash = new BCryptPasswordEncoder().encode(password);

        SharedItemsPublicEntity publicShare = new SharedItemsPublicEntity();
        publicShare.setItem(item);
        publicShare.setPermission(FileSystemPermission.VIEWER);
        publicShare.setSharedBy(item.getOwner());
        publicShare.setPasswordHash(hash);
        publicShare.setExpiresAt(Timestamp.from(Instant.now().plusSeconds(3600)));
        sharedItemsPublicRepository.save(publicShare);

        permissionService.validatePassword(item.getId(), password);
    }

    private FileSystemItemEntity createAndSaveFileItem() {
        FileSystemItemEntity item = new FileSystemItemEntity();
        item.setName("Public File");
        item.setSize(123L);
        item.setMimeType("text/plain");
        item.setOwner(UUID.randomUUID());
        item.setStatus(UploadStatus.UPLOADED);
        item.setType(FileSystemItemType.FILE);
        item.setS3Url("s3://test-bucket/test");
        return fileSystemItemRepository.save(item);
    }

}