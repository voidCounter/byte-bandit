package com.bytebandit.fileservice.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import com.bytebandit.fileservice.configurer.AbstractPostgresContainer;
import com.bytebandit.fileservice.enums.FileSystemItemType;
import com.bytebandit.fileservice.enums.FileSystemPermission;
import com.bytebandit.fileservice.enums.UploadStatus;
import com.bytebandit.fileservice.model.FileSystemItemEntity;
import com.bytebandit.fileservice.model.SharedItemsPrivateEntity;
import com.bytebandit.fileservice.model.UserSnapshotEntity;
import com.bytebandit.fileservice.repository.FileSystemItemRepository;
import com.bytebandit.fileservice.repository.SharedItemsPrivateRepository;
import com.bytebandit.fileservice.repository.UserSnapshotRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.servlet.ServletRequest;
import java.util.List;
import java.util.UUID;
import lib.core.enums.CustomHttpHeader;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
class PrivateShareControllerIT extends AbstractPostgresContainer {

    @LocalServerPort
    private int port;

    @Autowired
    private SharedItemsPrivateRepository sharedItemsPrivateRepository;

    @Autowired
    private UserSnapshotRepository userSnapshotRepository;

    @Autowired
    private FileSystemItemRepository fileSystemItemRepository;

    private final UUID ownerId = UUID.randomUUID();


    /**
     * Constructor for PrivateShareControllerIT.
     */
    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        sharedItemsPrivateRepository.deleteAll();
        userSnapshotRepository.deleteAll();
        fileSystemItemRepository.deleteAll();
    }

    private RequestSpecification requestSpecification() {
        return RestAssured.given()
            .contentType(ContentType.JSON);
    }

    /**
     * Test case for sharing an item with private permissions.
     *
     * <p>
     * Given a valid request body with itemId, sharedBy, sharedTo, and permissions,
     * When the request is sent to the /share/private endpoint,
     * Then the response status code should be 200 OK,
     * And the response body should contain a success message and permissionForEachUser.
     * </p>
     */
    @Test
    void shouldShareItemWithPrivatePermissionsSuccessfully() {
        String sharedBy = "test-1@gmail.com";

        UserSnapshotEntity userSnapshotEntity = new UserSnapshotEntity(ownerId, sharedBy);

        userSnapshotRepository.save(userSnapshotEntity);

        FileSystemItemEntity fileSystemItemEntity =
            fileSystemItemRepository.save(createAFile(ownerId));

        UserSnapshotEntity sharedWith = userSnapshotRepository.save(
            new UserSnapshotEntity(UUID.randomUUID(), "test-2@gmail.com")
        );

        SharedItemsPrivateEntity privateSharedItem = new SharedItemsPrivateEntity();
        privateSharedItem.setItem(fileSystemItemEntity);
        privateSharedItem.setUserId(ownerId);
        privateSharedItem.setSharedWith(sharedWith.getUserId());
        privateSharedItem.setPermission(FileSystemPermission.EDITOR);
        sharedItemsPrivateRepository.save(privateSharedItem);

        UUID itemId = fileSystemItemEntity.getId();

        String requestBody = """
            {
                "itemId": "%s",
                "sharedTo": ["%s"],
                "permissions": ["%s"]
            }
            """.formatted(itemId.toString(), sharedWith.getEmail(),
            FileSystemPermission.EDITOR.name());

        requestSpecification()
            .header(CustomHttpHeader.USER_ID.getValue(), ownerId)
            .body(requestBody)
            .when()
            .post("/share/private")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("status", equalTo(HttpStatus.OK.value()))
            .body("message", equalTo("Shared item successfully"))
            .body("data.permissionForEachUser", equalTo(List.of("ALLOWED")))
            .body("path", equalTo("/share/private"));
    }

    /**
     * Test case for sharing an item with private permissions when the user is not.
     * the owner or viewer.
     */
    @Test
    void shouldNotShareItemWithEditorPermission_IfNotOwnerOrViewer() {
        String sharedBy = "test-3@gmail.com";
        UserSnapshotEntity userSnapshotEntity = new UserSnapshotEntity(ownerId, sharedBy);

        userSnapshotRepository.save(userSnapshotEntity);

        FileSystemItemEntity fileSystemItemEntity =
            fileSystemItemRepository.save(createAFile(ownerId));

        UserSnapshotEntity sharedWithAsViewer = userSnapshotRepository.save(
            new UserSnapshotEntity(UUID.randomUUID(), "test-4@gmail.com")
        );
        
        SharedItemsPrivateEntity privateSharedItem = new SharedItemsPrivateEntity();
        privateSharedItem.setItem(fileSystemItemEntity);
        privateSharedItem.setUserId(ownerId);
        privateSharedItem.setSharedWith(sharedWithAsViewer.getUserId());
        privateSharedItem.setPermission(FileSystemPermission.VIEWER);
        sharedItemsPrivateRepository.save(privateSharedItem);

        UserSnapshotEntity sharedWithAsEditor = userSnapshotRepository.save(
            new UserSnapshotEntity(UUID.randomUUID(), "test-5@gmail.com")
        );

        UUID itemId = fileSystemItemEntity.getId();

        String requestBody = """
            {
                "itemId": "%s",
                "sharedTo": ["%s"],
                "permissions": ["%s"]
            }
            """.formatted(itemId.toString(),
            sharedWithAsEditor.getEmail(), FileSystemPermission.EDITOR.name());

        requestSpecification()
            .header(CustomHttpHeader.USER_ID.getValue(), sharedWithAsViewer.getUserId())
            .body(requestBody)
            .when()
            .post("/share/private")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("status", equalTo(HttpStatus.OK.value()))
            .body("message", equalTo("Shared item successfully"))
            .body("data.permissionForEachUser", equalTo(List.of("NOT_ALLOWED")))
            .body("path", equalTo("/share/private"));
    }


    @Test
    void canNotMakeShareReuqestWithoutUserIdHeader() {
        String sharedBy = "test-6@gmail.com";

        UserSnapshotEntity userSnapshotEntity = new UserSnapshotEntity(ownerId, sharedBy);
        userSnapshotRepository.save(userSnapshotEntity);

        FileSystemItemEntity fileSystemItemEntity =
            fileSystemItemRepository.save(createAFile(ownerId));

        UserSnapshotEntity sharedWith = userSnapshotRepository.save(
            new UserSnapshotEntity(UUID.randomUUID(), "test-7@gmail.com")
        );

        UUID itemId = fileSystemItemEntity.getId();

        String requestBody = """
            {
                "itemId": "%s",
                "sharedTo": ["%s"],
                "permissions": ["%s"]
            }
            """.formatted(itemId.toString(), sharedWith.getEmail(),
            FileSystemPermission.VIEWER.name());

        requestSpecification()
            .body(requestBody)
            .when()
            .post("/share/private")
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .body("status", CoreMatchers.equalTo(401))
            .body("errorCode", CoreMatchers.equalTo("HEADER-01"))
            .body("message", containsString("Required header is missing."));
    }

    /**
     * Test case for sharing an item with private permissions when the user is not.
     * the owner or viewer.
     */
    FileSystemItemEntity createAFile(UUID ownerId) {
        FileSystemItemEntity fileSystemItemEntity = new FileSystemItemEntity();
        fileSystemItemEntity.setChunks(null);
        fileSystemItemEntity.setMimeType("text/plain");
        fileSystemItemEntity.setName("test.txt");
        fileSystemItemEntity.setOwner(ownerId);
        fileSystemItemEntity.setS3Url("url");
        fileSystemItemEntity.setSize(1024L);
        fileSystemItemEntity.setStatus(UploadStatus.NOT_UPLOADED);
        fileSystemItemEntity.setType(FileSystemItemType.FILE);
        fileSystemItemEntity.setParent(null);
        return fileSystemItemEntity;
    }
}
