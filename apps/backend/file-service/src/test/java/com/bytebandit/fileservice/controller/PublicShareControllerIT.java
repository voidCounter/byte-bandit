package com.bytebandit.fileservice.controller;

import static org.hamcrest.Matchers.equalTo;

import com.bytebandit.fileservice.configurer.AbstractPostgresContainer;
import com.bytebandit.fileservice.enums.FileSystemItemType;
import com.bytebandit.fileservice.enums.FileSystemPermission;
import com.bytebandit.fileservice.enums.UploadStatus;
import com.bytebandit.fileservice.model.FileSystemItemEntity;
import com.bytebandit.fileservice.model.SharedItemsPrivateEntity;
import com.bytebandit.fileservice.model.SharedItemsPublicEntity;
import com.bytebandit.fileservice.model.UserSnapshotEntity;
import com.bytebandit.fileservice.repository.FileSystemItemRepository;
import com.bytebandit.fileservice.repository.SharedItemsPrivateRepository;
import com.bytebandit.fileservice.repository.SharedItemsPublicRepository;
import com.bytebandit.fileservice.repository.UserSnapshotRepository;
import com.bytebandit.fileservice.utils.Messages;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;
import lib.core.enums.CustomHttpHeader;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class PublicShareControllerIT extends AbstractPostgresContainer {
    @LocalServerPort
    private int port;
    
    @Autowired
    private UserSnapshotRepository userSnapshotRepository;
    
    @Autowired
    private FileSystemItemRepository fileSystemItemRepository;
    
    @Autowired
    private SharedItemsPublicRepository sharedItemsPublicRepository;
    
    @Autowired
    private SharedItemsPrivateRepository sharedItemsPrivateRepository;
    
    private final UUID ownerId = UUID.randomUUID();
    private final UUID editorId = UUID.randomUUID();
    private final UUID viewerId = UUID.randomUUID();
    
    /**
     * Setup method for the test class.
     */
    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        userSnapshotRepository.deleteAll();
        fileSystemItemRepository.deleteAll();
        sharedItemsPrivateRepository.deleteAll();
        sharedItemsPublicRepository.deleteAll();
    }
    
    private RequestSpecification requestSpecification() {
        return RestAssured.given()
            .contentType(MediaType.APPLICATION_JSON_VALUE);
    }
    
    /**
     * Test for sharing an item with password and expiresAt as owner.
     */
    @Test
    void shouldShareItemWithPasswordAndExpiresAtAsOwner() {
        String userEmail = "test-public@gmail.com";
        UserSnapshotEntity userSnapshotEntity = new UserSnapshotEntity(ownerId, userEmail);
        userSnapshotRepository.save(userSnapshotEntity);
        
        FileSystemItemEntity fileSystemItemEntity =
            fileSystemItemRepository.save(createAFile(ownerId, null));
        UUID itemId = fileSystemItemEntity.getId();
        
        String requestBody = """
            {
                "itemId": "%s",
                "permission": "%s",
                "password": "securePass123",
                "expiresAt": "2025-12-31T23:59:59.000Z"
            }
            """.formatted(itemId.toString(), FileSystemPermission.VIEWER.name());
        
        requestSpecification()
            .header(CustomHttpHeader.USER_ID.getValue(), ownerId)
            .body(requestBody)
            .when()
            .post("/share/public")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("status", equalTo(HttpStatus.OK.value()))
            .body("message", equalTo(Messages.ITEM_SHARED_WITH_PASSWORD_AND_EXPIRES_AT))
            .body("data.link", CoreMatchers.notNullValue())
            .body("data.permission", equalTo(FileSystemPermission.VIEWER.name()))
            .body("path", equalTo("/share/public"));
    }
    
    /**
     * Test for sharing an item with password only as owner.
     */
    @Test
    void shouldShareItemWithPasswordOnlyAsOwner() {
        String userEmail = "test-public@gmail.com";
        UserSnapshotEntity userSnapshotEntity = new UserSnapshotEntity(ownerId, userEmail);
        userSnapshotRepository.save(userSnapshotEntity);
        
        FileSystemItemEntity fileSystemItemEntity =
            fileSystemItemRepository.save(createAFile(ownerId, null));
        UUID itemId = fileSystemItemEntity.getId();
        
        String requestBody = """
            {
                "itemId": "%s",
                "permission": "%s",
                "password": "securePass123"
            }
            """.formatted(itemId.toString(), FileSystemPermission.VIEWER.name());
        
        requestSpecification()
            .header(CustomHttpHeader.USER_ID.getValue(), ownerId)
            .body(requestBody)
            .when()
            .post("/share/public")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("status", equalTo(HttpStatus.OK.value()))
            .body("message", equalTo(Messages.ITEM_SHARED_WITH_PASSWORD))
            .body("data.link", CoreMatchers.notNullValue())
            .body("data.permission", equalTo(FileSystemPermission.VIEWER.name()))
            .body("path", equalTo("/share/public"));
    }
    
    /**
     * Test for sharing an item with expiresAt only as owner.
     */
    @Test
    void shouldShareItemWithExpiresAtOnlyAsOwner() {
        String userEmail = "test-public@gmail.com";
        UserSnapshotEntity userSnapshotEntity = new UserSnapshotEntity(ownerId, userEmail);
        userSnapshotRepository.save(userSnapshotEntity);
        
        FileSystemItemEntity fileSystemItemEntity =
            fileSystemItemRepository.save(createAFile(ownerId, null));
        UUID itemId = fileSystemItemEntity.getId();
        
        String requestBody = """
            {
                "itemId": "%s",
                "permission": "%s",
                "expiresAt": "2025-12-31T23:59:59.000Z"
            }
            """.formatted(itemId.toString(), FileSystemPermission.VIEWER.name());
        
        requestSpecification()
            .header(CustomHttpHeader.USER_ID.getValue(), ownerId)
            .body(requestBody)
            .when()
            .post("/share/public")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("status", equalTo(HttpStatus.OK.value()))
            .body("message", equalTo(Messages.ITEM_SHARED_WITH_EXPIRES_AT))
            .body("data.link", CoreMatchers.notNullValue())
            .body("data.permission", equalTo(FileSystemPermission.VIEWER.name()))
            .body("path", equalTo("/share/public"));
    }
    
    /**
     * Test for sharing an item without password and expiresAt as owner.
     */
    @Test
    void shouldShareItemWithoutPasswordOrExpiresAtAsOwner() {
        String userEmail = "test-public@gmail.com";
        UserSnapshotEntity userSnapshotEntity = new UserSnapshotEntity(ownerId, userEmail);
        userSnapshotRepository.save(userSnapshotEntity);
        
        FileSystemItemEntity fileSystemItemEntity =
            fileSystemItemRepository.save(createAFile(ownerId, null));
        UUID itemId = fileSystemItemEntity.getId();
        
        String requestBody = """
            {
                "itemId": "%s",
                "permission": "%s"
            }
            """.formatted(itemId.toString(), FileSystemPermission.VIEWER.name());
        
        requestSpecification()
            .header(CustomHttpHeader.USER_ID.getValue(), ownerId)
            .body(requestBody)
            .when()
            .post("/share/public")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("status", equalTo(HttpStatus.OK.value()))
            .body("message", equalTo(Messages.ITEM_SHARED_SUCCESSFULLY))
            .body("data.link", CoreMatchers.notNullValue())
            .body("data.permission", equalTo(FileSystemPermission.VIEWER.name()))
            .body("path", equalTo("/share/public"));
    }
    
    /**
     * Test for sharing an item as a direct editor.
     */
    @Test
    void shouldShareItemAsDirectPublicEditor() {
        String ownerEmail = "owner@gmail.com";
        UserSnapshotEntity ownerEntity = new UserSnapshotEntity(ownerId, ownerEmail);
        userSnapshotRepository.save(ownerEntity);
        
        String editorEmail = "public_editor@gmail.com";
        UserSnapshotEntity editorEntity = new UserSnapshotEntity(editorId, editorEmail);
        userSnapshotRepository.save(editorEntity);
        
        FileSystemItemEntity fileSystemItemEntity =
            fileSystemItemRepository.save(createAFile(ownerId, null));
        // Grant editor permission to editorId on the item
        SharedItemsPublicEntity publicShare = new SharedItemsPublicEntity();
        publicShare.setItem(fileSystemItemEntity);
        publicShare.setSharedBy(ownerId);
        publicShare.setPermission(FileSystemPermission.EDITOR);
        publicShare.setCreatedAt(Timestamp.from(Instant.now()));
        publicShare.setUpdatedAt(Timestamp.from(Instant.now()));
        sharedItemsPublicRepository.save(publicShare);
        
        UUID itemId = fileSystemItemEntity.getId();
        String requestBody = """
            {
                "itemId": "%s",
                "permission": "%s"
            }
            """.formatted(itemId.toString(), FileSystemPermission.VIEWER.name());
        
        requestSpecification()
            .header(CustomHttpHeader.USER_ID.getValue(), editorId)
            .body(requestBody)
            .when()
            .post("/share/public")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("status", equalTo(HttpStatus.OK.value()))
            .body("message", equalTo(Messages.ITEM_SHARED_SUCCESSFULLY))
            .body("data.link", CoreMatchers.notNullValue())
            .body("data.permission", equalTo(FileSystemPermission.VIEWER.name()))
            .body("path", equalTo("/share/public"));
    }
    
    /**
     * Test for sharing an item as a direct editor.
     */
    @Test
    void shouldShareItemAsDirectEditor() {
        String ownerEmail = "owner@gmail.com";
        UserSnapshotEntity ownerEntity = new UserSnapshotEntity(ownerId, ownerEmail);
        userSnapshotRepository.save(ownerEntity);
        
        String editorEmail = "editor@gmail.com";
        UserSnapshotEntity editorEntity = new UserSnapshotEntity(editorId, editorEmail);
        userSnapshotRepository.save(editorEntity);
        
        FileSystemItemEntity fileSystemItemEntity =
            fileSystemItemRepository.save(createAFile(ownerId, null));
        // Grant editor permission to editorId on the item
        SharedItemsPrivateEntity privateShare = new SharedItemsPrivateEntity();
        privateShare.setItem(fileSystemItemEntity);
        privateShare.setSharedWith(editorId);
        privateShare.setPermission(FileSystemPermission.EDITOR);
        privateShare.setUserId(ownerId);
        privateShare.setCreatedAt(Timestamp.from(Instant.now()));
        privateShare.setUpdatedAt(Timestamp.from(Instant.now()));
        sharedItemsPrivateRepository.save(privateShare);
        
        UUID itemId = fileSystemItemEntity.getId();
        String requestBody = """
            {
                "itemId": "%s",
                "permission": "%s",
                "password": "securePass123",
                "expiresAt": "2025-12-31T23:59:59.000Z"
            }
            """.formatted(itemId.toString(), FileSystemPermission.VIEWER.name());
        
        requestSpecification()
            .header(CustomHttpHeader.USER_ID.getValue(), editorId)
            .body(requestBody)
            .when()
            .post("/share/public")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("status", equalTo(HttpStatus.OK.value()))
            .body("message", equalTo(Messages.ITEM_SHARED_SUCCESSFULLY))
            .body("data.link", CoreMatchers.notNullValue())
            .body("data.permission", equalTo(FileSystemPermission.VIEWER.name()))
            .body("path", equalTo("/share/public"));
    }
    
    /**
     * Test for sharing an item as a parent editor.
     */
    @Test
    void shouldShareItemAsParentEditor() {
        String ownerEmail = "owner@gmail.com";
        UserSnapshotEntity ownerEntity = new UserSnapshotEntity(ownerId, ownerEmail);
        userSnapshotRepository.save(ownerEntity);
        
        String editorEmail = "editor@gmail.com";
        UserSnapshotEntity editorEntity = new UserSnapshotEntity(editorId, editorEmail);
        userSnapshotRepository.save(editorEntity);
        
        // Create a parent folder
        FileSystemItemEntity parentFolder = new FileSystemItemEntity();
        parentFolder.setOwner(ownerId);
        parentFolder.setName("parent_folder");
        parentFolder.setType(FileSystemItemType.FOLDER);
        parentFolder.setParent(null);
        parentFolder.setStatus(UploadStatus.UPLOADED);
        parentFolder = fileSystemItemRepository.save(parentFolder);
        FileSystemItemEntity file = createAFile(ownerId, parentFolder);
        fileSystemItemRepository.save(file);
        
        // Grant editor permission to editorId on the parent folder
        SharedItemsPrivateEntity privateShare = new SharedItemsPrivateEntity();
        privateShare.setItem(parentFolder);
        privateShare.setSharedWith(editorId);
        privateShare.setPermission(FileSystemPermission.EDITOR);
        privateShare.setUserId(ownerId);
        privateShare.setCreatedAt(Timestamp.from(Instant.now()));
        privateShare.setUpdatedAt(Timestamp.from(Instant.now()));
        sharedItemsPrivateRepository.save(privateShare);
        
        UUID itemId = file.getId();
        String requestBody = """
            {
                "itemId": "%s",
                "permission": "%s"
            }
            """.formatted(itemId.toString(), FileSystemPermission.VIEWER.name());
        
        requestSpecification()
            .header(CustomHttpHeader.USER_ID.getValue(), editorId)
            .body(requestBody)
            .when()
            .post("/share/public")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("status", equalTo(HttpStatus.OK.value()))
            .body("message", equalTo(Messages.ITEM_SHARED_SUCCESSFULLY))
            .body("data.link", CoreMatchers.notNullValue())
            .body("data.permission", equalTo(FileSystemPermission.VIEWER.name()))
            .body("path", equalTo("/share/public"));
    }
    
    /**
     * Test for sharing an item as a viewer.
     */
    @Test
    void shouldFailWithViewerPermission() {
        String ownerEmail = "owner@gmail.com";
        UserSnapshotEntity ownerEntity = new UserSnapshotEntity(ownerId, ownerEmail);
        userSnapshotRepository.save(ownerEntity);
        
        String viewerEmail = "viewer@gmail.com";
        UserSnapshotEntity viewerEntity = new UserSnapshotEntity(viewerId, viewerEmail);
        userSnapshotRepository.save(viewerEntity);
        
        FileSystemItemEntity fileSystemItemEntity =
            fileSystemItemRepository.save(createAFile(ownerId, null));
        
        // Grant viewer permission to viewerId on the item
        SharedItemsPrivateEntity privateShare = new SharedItemsPrivateEntity();
        privateShare.setItem(fileSystemItemEntity);
        privateShare.setSharedWith(viewerId);
        privateShare.setPermission(FileSystemPermission.VIEWER);
        privateShare.setUserId(ownerId);
        privateShare.setCreatedAt(Timestamp.from(Instant.now()));
        privateShare.setUpdatedAt(Timestamp.from(Instant.now()));
        sharedItemsPrivateRepository.save(privateShare);
        
        UUID itemId = fileSystemItemEntity.getId();
        String requestBody = """
            {
                "itemId": "%s",
                "permission": "%s"
            }
            """.formatted(itemId.toString(), FileSystemPermission.VIEWER.name());
        
        requestSpecification()
            .header(CustomHttpHeader.USER_ID.getValue(), viewerId)
            .body(requestBody)
            .when()
            .post("/share/public")
            .then()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .body("status", equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value()))
            .body("errorCode", equalTo("SHARE-01"))
            .body("details", equalTo(Messages.USER_NOT_AUTHORIZED_TO_SHARE));
    }
    
    /**
     * Test for sharing an item with no access.
     */
    @Test
    void shouldFailWithNoAccess() {
        String ownerEmail = "owner@gmail.com";
        UserSnapshotEntity ownerEntity = new UserSnapshotEntity(ownerId, ownerEmail);
        userSnapshotRepository.save(ownerEntity);
        
        String otherUserEmail = "other@gmail.com";
        UserSnapshotEntity otherUserEntity = new UserSnapshotEntity(editorId, otherUserEmail);
        userSnapshotRepository.save(otherUserEntity);
        
        FileSystemItemEntity fileSystemItemEntity =
            fileSystemItemRepository.save(createAFile(ownerId, null));
        UUID itemId = fileSystemItemEntity.getId();
        
        String requestBody = """
            {
                "itemId": "%s",
                "permission": "%s"
            }
            """.formatted(itemId.toString(), FileSystemPermission.VIEWER.name());
        
        requestSpecification()
            .header(CustomHttpHeader.USER_ID.getValue(), editorId)
            .body(requestBody)
            .when()
            .post("/share/public")
            .then()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .body("status", equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value()))
            .body("errorCode", equalTo("SHARE-01"))
            .body("details", equalTo(Messages.USER_NOT_AUTHORIZED_TO_SHARE));
    }
    
    /**
     * Test for sharing an item with an invalid user.
     */
    @Test
    void shouldFailWithInvalidUser() {
        String userEmail = "test-public@gmail.com";
        UserSnapshotEntity userSnapshotEntity = new UserSnapshotEntity(ownerId, userEmail);
        userSnapshotRepository.save(userSnapshotEntity);
        
        FileSystemItemEntity fileSystemItemEntity =
            fileSystemItemRepository.save(createAFile(ownerId, null));
        UUID itemId = fileSystemItemEntity.getId();
        
        UUID invalidUserId = UUID.randomUUID();
        String requestBody = """
            {
                "itemId": "%s",
                "permission": "%s"
            }
            """.formatted(itemId.toString(), FileSystemPermission.VIEWER.name());
        
        requestSpecification()
            .header(CustomHttpHeader.USER_ID.getValue(), invalidUserId)
            .body(requestBody)
            .when()
            .post("/share/public")
            .then()
            .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .body("status", equalTo(HttpStatus.INTERNAL_SERVER_ERROR.value()))
            .body("errorCode", equalTo("SHARE-01"))
            .body("details", equalTo(Messages.USER_NOT_AUTHORIZED_TO_SHARE));
    }
    
    
    private FileSystemItemEntity createAFile(UUID owner, FileSystemItemEntity parent) {
        FileSystemItemEntity entity = new FileSystemItemEntity();
        entity.setOwner(owner);
        entity.setName("test.txt");
        entity.setType(FileSystemItemType.FILE);
        entity.setMimeType("text/plain");
        entity.setS3Url("s3://bucket/test.txt");
        entity.setSize(1024L);
        entity.setStatus(UploadStatus.UPLOADED);
        entity.setParent(parent);
        return entity;
    }
}