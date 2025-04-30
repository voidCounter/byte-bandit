package com.bytebandit.fileservice.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

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
import com.bytebandit.fileservice.service.RoleBasedAccessControlService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import lib.core.enums.CustomHttpHeader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class ItemViewControllerIT extends AbstractPostgresContainer {

    @LocalServerPort
    private int port;

    @Autowired
    private FileSystemItemRepository fileSystemItemRepository;

    @Autowired
    private UserSnapshotRepository userSnapshotRepository;

    @Autowired
    private SharedItemsPrivateRepository sharedItemsPrivateRepository;

    @Autowired
    private SharedItemsPublicRepository sharedItemsPublicRepository;

    @MockitoBean
    private RoleBasedAccessControlService roleBasedAccessControlService;

    private final UUID ownerId = UUID.randomUUID();
    private final UUID anotherUserId = UUID.randomUUID();
    private final String rawPassword = "secure-password";
    private final String passwordHash = new BCryptPasswordEncoder().encode(rawPassword);

    /**
     * This method sets up the test environment by configuring the RestAssured port and.
     * clearing the database before each test.
     */
    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        fileSystemItemRepository.deleteAll();
        userSnapshotRepository.deleteAll();
        sharedItemsPublicRepository.deleteAll();
        sharedItemsPrivateRepository.deleteAll();
    }

    private RequestSpecification requestSpecification() {
        return RestAssured.given()
            .contentType(ContentType.JSON);
    }

    /**
     * This test verifies that the item view request is handled successfully when the user has
     * access to the item.
     */
    @Test
    void shouldReturnItemViewSuccessfully_WhenUserHasAccess() {
        userSnapshotRepository.save(
            new UserSnapshotEntity(ownerId, "owner-1@example.com")
        );

        FileSystemItemEntity fileItem = createAFolder(ownerId, "Parent Folder");
        fileItem.setParent(null);
        fileSystemItemRepository.save(fileItem);

        FileSystemItemEntity childItem = createAFolder(ownerId, "Chlid Folder - 1");
        childItem.setParent(fileItem);
        fileSystemItemRepository.save(childItem);

        FileSystemItemEntity childItem2 = createAFolder(ownerId, "Child Folder - 2");
        childItem2.setParent(fileItem);
        fileSystemItemRepository.save(childItem2);


        Mockito.when(roleBasedAccessControlService.getPermission(fileItem.getId().toString(),
                ownerId))
            .thenReturn("OWNER");

        String requestBody = """
            {
                "itemId": "%s"
            }
            """.formatted(fileItem.getId());

        requestSpecification()
            .header(CustomHttpHeader.USER_ID.getValue(), ownerId.toString())
            .body(requestBody)
            .when()
            .post("/view")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("status", equalTo(HttpStatus.OK.value()))
            .body("message", equalTo("Viewed item successfully"))
            .body("data.name", equalTo(fileItem.getName()))
            .body("data.itemId", equalTo(fileItem.getId().toString()))
            .body("data.mimeType", equalTo(fileItem.getMimeType()))
            .body("data.children", notNullValue());
    }

    /**
     * This test verifies that the item view request returns an error when the user does not have
     * access to the item.
     */
    @Test
    void shouldReturnError_WhenItemNotFound() {
        userSnapshotRepository.save(
            new UserSnapshotEntity(ownerId, "user@example.com")
        );

        UUID randomItemId = UUID.randomUUID();

        Mockito.when(roleBasedAccessControlService.getPermission(randomItemId.toString(), ownerId))
            .thenReturn("OWNER");

        String requestBody = """
            {
                "itemId": "%s"
            }
            """.formatted(randomItemId);

        requestSpecification()
            .header(CustomHttpHeader.USER_ID.getValue(), ownerId.toString())
            .body(requestBody)
            .when()
            .post("/view")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("errorCode", equalTo("ITEM-02"))
            .body("error", equalTo("Not Found"))
            .body("message", equalTo("Error occurred while viewing the item."));
    }

    /**
     * Any user with permission editor can view the item.
     */
    @Test
    void shouldAllowEditorToViewSharedFolderWithChildrenSuccessfully() {
        userSnapshotRepository.save(
            new UserSnapshotEntity(ownerId, "owner-3@mail.com")
        );

        FileSystemItemEntity parentFolder = FileSystemItemEntity.builder()
            .name("shared_folder")
            .type(FileSystemItemType.FOLDER)
            .owner(ownerId)
            .status(UploadStatus.NOT_UPLOADED)
            .build();

        final FileSystemItemEntity savedParent = fileSystemItemRepository.save(parentFolder);


        UUID newUserId = UUID.randomUUID();
        userSnapshotRepository.save(
            new UserSnapshotEntity(newUserId, "editor-3@mail.com")
        );

        SharedItemsPrivateEntity sharedItemsPrivateEntity = SharedItemsPrivateEntity.builder()
            .sharedWith(newUserId)
            .permission(FileSystemPermission.EDITOR)
            .item(parentFolder)
            .userId(ownerId)
            .build();

        sharedItemsPrivateRepository.save(sharedItemsPrivateEntity);

        Mockito.when(roleBasedAccessControlService.getPermission(
                savedParent.getId().toString(), newUserId))
            .thenReturn("EDITOR");

        List<FileSystemItemEntity> childFiles = IntStream.range(1, 4)
            .mapToObj(i -> FileSystemItemEntity.builder()
                .name("child_file_" + i + ".txt")
                .type(FileSystemItemType.FILE)
                .size(1024L)
                .owner(newUserId)
                .status(UploadStatus.UPLOADED)
                .mimeType("text/plain")
                .s3Url("https://s3.com/child_file_" + i + ".txt")
                .parent(savedParent)
                .build())
            .toList();
        fileSystemItemRepository.saveAll(childFiles);

        String requestBody = """
            {
                "itemId": "%s"
            }
            """.formatted(parentFolder.getId());

        requestSpecification()
            .header(CustomHttpHeader.USER_ID.getValue(), newUserId)
            .body(requestBody)
            .when()
            .post("/view")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("status", equalTo(HttpStatus.OK.value()))
            .body("message", equalTo("Viewed item successfully"))
            .body("data.itemId", equalTo(parentFolder.getId().toString()))
            .body("data.permission", equalTo("EDITOR"))
            .body("data.name", equalTo("shared_folder"))
            .body("data.children.size()", equalTo(3));
    }

    /**    /**
     * This test verifies that the item view request returns an error when the user does not have
     * access to the item.
     */
    @Test
    void shouldAllowPublicUserToViewItem_WhenOwnerSharedPublicly() {
        userSnapshotRepository.save(
            new UserSnapshotEntity(ownerId, "owner@domain.com")
        );

        FileSystemItemEntity publicFolder = FileSystemItemEntity.builder()
            .name("public_folder")
            .type(FileSystemItemType.FOLDER)
            .owner(ownerId)
            .status(UploadStatus.UPLOADED)
            .build();
        fileSystemItemRepository.save(publicFolder);

        UUID publicUserId = UUID.randomUUID();
        userSnapshotRepository.save(
            new UserSnapshotEntity(publicUserId, "public-user@domain.com")
        );

        Mockito.when(roleBasedAccessControlService.getPermission(
            publicFolder.getId().toString(), publicUserId
            )).thenReturn("VIEWER");

        SharedItemsPublicEntity publicShare = SharedItemsPublicEntity.builder()
            .permission(FileSystemPermission.VIEWER)
            .sharedBy(ownerId)
            .item(publicFolder)
            .build();

        sharedItemsPublicRepository.save(publicShare);

        FileSystemItemEntity childFile = FileSystemItemEntity.builder()
            .name("shared_doc.txt")
            .type(FileSystemItemType.FILE)
            .owner(ownerId)
            .size(1024L)
            .status(UploadStatus.UPLOADED)
            .mimeType("text/plain")
            .s3Url("https://s3.com/shared_doc.txt")
            .parent(publicFolder)
            .build();
        fileSystemItemRepository.save(childFile);

        String requestBody = """
            {
                "itemId": "%s"
            }
            """.formatted(publicFolder.getId());

        requestSpecification()
            .header(CustomHttpHeader.USER_ID.getValue(), publicUserId)
            .body(requestBody)
            .when()
            .post("/view")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("status", equalTo(HttpStatus.OK.value()))
            .body("message", equalTo("Viewed item successfully"))
            .body("data.itemId", equalTo(publicFolder.getId().toString()))
            .body("data.name", equalTo("public_folder"))
            .body("data.permission", equalTo("VIEWER"))
            .body("data.children.size()", equalTo(1));
    }

    /**
     * This test verifies that the item view request returns an error when the user does not have
     * access to the item.
     */
    @Test
    void shouldRejectAccess_WhenPasswordProtectedItemIsAccessedWithoutPassword() {

        userSnapshotRepository.save(new UserSnapshotEntity(ownerId, "owner@domain.com"));
        userSnapshotRepository.save(new UserSnapshotEntity(anotherUserId, "guest@domain.com"));

        FileSystemItemEntity folder = FileSystemItemEntity.builder()
            .name("Shared Folder")
            .type(FileSystemItemType.FOLDER)
            .owner(ownerId)
            .status(UploadStatus.UPLOADED)
            .build();
        folder = fileSystemItemRepository.save(folder);

        sharedItemsPublicRepository.save(
            SharedItemsPublicEntity.builder()
                .sharedBy(ownerId)
                .passwordHash(passwordHash)
                .permission(FileSystemPermission.EDITOR)
                .item(folder)
                .expiresAt(Timestamp.from(Instant.now().plus(Duration.ofHours(1))))
                .build()
        );

        Mockito.when(roleBasedAccessControlService.getPermission(
            folder.getId().toString(),
            anotherUserId
        )).thenReturn("EDITOR");

        Mockito.when(roleBasedAccessControlService.isPasswordProtected(
            folder.getId().toString()
        )).thenReturn(true);

        doThrow(
            new ItemProtectedWithPasswordException("The item you are trying to access is "
                + "protected by  password")
        ).when(
            roleBasedAccessControlService
        ).validatePassword(folder.getId(), null);

        String requestBody = """
            {
                "itemId": "%s"
            }
            """.formatted(folder.getId().toString());

        requestSpecification()
            .header(CustomHttpHeader.USER_ID.getValue(), anotherUserId)
            .body(requestBody)
            .when()
            .post("/view")
            .then()
            .statusCode(HttpStatus.FORBIDDEN.value())
            .body("errorCode", equalTo("ITEM-03"))
            .body("message", containsString("protected item"));
    }

    /**
     * This test verifies that the item view request returns an error when the user does not have
     * access to the item.
     */
    @Test
    void shouldRejectAccess_WhenWrongPasswordIsProvided() {

        userSnapshotRepository.save(new UserSnapshotEntity(ownerId, "owner@domain.com"));
        userSnapshotRepository.save(new UserSnapshotEntity(anotherUserId, "guest@domain.com"));

        FileSystemItemEntity folder = FileSystemItemEntity.builder()
            .name("Shared Folder")
            .type(FileSystemItemType.FOLDER)
            .owner(ownerId)
            .status(UploadStatus.UPLOADED)
            .build();
        folder = fileSystemItemRepository.save(folder);

        sharedItemsPublicRepository.save(
            SharedItemsPublicEntity.builder()
                .sharedBy(ownerId)
                .passwordHash(passwordHash)
                .permission(FileSystemPermission.EDITOR)
                .item(folder)
                .expiresAt(Timestamp.from(Instant.now().plus(Duration.ofHours(1))))
                .build()
        );

        Mockito.when(roleBasedAccessControlService.getPermission(
            folder.getId().toString(),
            anotherUserId
        )).thenReturn("EDITOR");

        Mockito.when(roleBasedAccessControlService.isPasswordProtected(
            folder.getId().toString()
        )).thenReturn(true);

        String wrongPassword = "wrong-password";
        doThrow(
            new ItemPasswordVerificationFailedException("Provided password is wrong")
        ).when(
            roleBasedAccessControlService
        ).validatePassword(folder.getId(), wrongPassword);

        String requestBody = """
            {
                "itemId": "%s",
                "password": "%s"
            }
            """.formatted(folder.getId().toString(), wrongPassword);

        requestSpecification()
            .header(CustomHttpHeader.USER_ID.getValue(), anotherUserId)
            .body(requestBody)
            .when()
            .post("/view")
            .then()
            .statusCode(HttpStatus.FORBIDDEN.value())
            .body("errorCode", equalTo("ITEM-04"))
            .body("message", containsString("Wrong password"));
    }

    /**
     * This test verifies that the item view request returns an error when the user does not have
     * access to the item.
     */
    @Test
    void shouldAllowAccess_WhenCorrectPasswordIsProvided() {

        userSnapshotRepository.save(new UserSnapshotEntity(ownerId, "owner@domain.com"));
        userSnapshotRepository.save(new UserSnapshotEntity(anotherUserId, "guest@domain.com"));

        FileSystemItemEntity folder = FileSystemItemEntity.builder()
            .name("Shared Folder")
            .type(FileSystemItemType.FOLDER)
            .owner(ownerId)
            .status(UploadStatus.UPLOADED)
            .build();
        folder = fileSystemItemRepository.save(folder);

        sharedItemsPublicRepository.save(
            SharedItemsPublicEntity.builder()
                .sharedBy(ownerId)
                .passwordHash(passwordHash)
                .permission(FileSystemPermission.EDITOR)
                .item(folder)
                .expiresAt(Timestamp.from(Instant.now().plus(Duration.ofHours(1))))
                .build()
        );

        Mockito.when(roleBasedAccessControlService.getPermission(
            folder.getId().toString(),
            anotherUserId
        )).thenReturn("EDITOR");

        Mockito.when(roleBasedAccessControlService.isPasswordProtected(
            folder.getId().toString()
        )).thenReturn(true);

        doNothing()
            .when(roleBasedAccessControlService)
            .validatePassword(folder.getId(), rawPassword);

        String requestBody = """
            {
                "itemId": "%s",
                "password": "%s"
            }
            """.formatted(folder.getId().toString(), rawPassword);

        requestSpecification()
            .header(CustomHttpHeader.USER_ID.getValue(), anotherUserId)
            .body(requestBody)
            .when()
            .post("/view")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("status", equalTo(200))
            .body("data.itemId", equalTo(folder.getId().toString()))
            .body("data.name", equalTo("Shared Folder"))
            .body("data.permission", equalTo("EDITOR"));
    }

    private FileSystemItemEntity createAFolder(UUID ownerId, String folderName) {
        return FileSystemItemEntity.builder()
            .name(folderName)
            .status(UploadStatus.UPLOADED)
            .type(FileSystemItemType.FOLDER)
            .owner(ownerId)
            .build();
    }
}
