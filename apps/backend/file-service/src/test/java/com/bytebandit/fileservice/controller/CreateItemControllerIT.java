package com.bytebandit.fileservice.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.bytebandit.fileservice.configurer.AbstractPostgresContainer;
import com.bytebandit.fileservice.enums.FileSystemItemType;
import com.bytebandit.fileservice.enums.UploadStatus;
import com.bytebandit.fileservice.model.FileSystemItemEntity;
import com.bytebandit.fileservice.model.UserSnapshotEntity;
import com.bytebandit.fileservice.repository.FileSystemItemRepository;
import com.bytebandit.fileservice.repository.UserSnapshotRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.util.UUID;
import lib.core.enums.CustomHttpHeader;
import lombok.extern.slf4j.Slf4j;
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
class CreateItemControllerIT extends AbstractPostgresContainer {
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private FileSystemItemRepository fileSystemItemRepository;
    
    @Autowired
    private UserSnapshotRepository userSnapshotRepository;
    
    private final UUID ownerId = UUID.randomUUID();
    
    
    /**
     * This method is used to set up the test environment before each test. It initializes the
     * RestAssured port and clears the database.
     */
    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        fileSystemItemRepository.deleteAll();
        userSnapshotRepository.deleteAll();
    }
    
    private RequestSpecification requestSpecification() {
        return RestAssured.given()
            .contentType(ContentType.JSON);
    }
    
    /**
     * Create a folder with the given owner ID.
     */
    @Test
    void shouldCreateItemSuccessfully_WhenOwnerOrEditor() {
        userSnapshotRepository.save(
            new UserSnapshotEntity(ownerId, "valid-mail@domain.com")
        );
        FileSystemItemEntity parentFolder = createAFolder(ownerId);
        fileSystemItemRepository.save(parentFolder);
        
        String requestBody = """
            {
                "name": "new_file.txt",
                "size": 1234,
                "mimeType": "text/plain",
                "status": "UPLOADED",
                "type": "FILE",
                "chunks": null,
                "s3Url": "https://s3.bucket.com/new_file.txt",
                "parentId": "%s"
            }
            """.formatted(parentFolder.getId());
        
        requestSpecification()
            .header(CustomHttpHeader.USER_ID.getValue(), ownerId)
            .body(requestBody)
            .when()
            .post("/create")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("status", equalTo(HttpStatus.OK.value()))
            .body("message", equalTo("Created item successfully"))
            .body("data.name", equalTo("new_file.txt"))
            .body("data.parentId", equalTo(parentFolder.getId().toString()))
            .body("data.mimeType", equalTo("text/plain"))
            .body("data.size", equalTo(1234))
            .body("data.status", equalTo("UPLOADED"))
            .body("data.type", equalTo("FILE"));
    }
    
    /**
     * When user has no permission to create an item, the request should fail. with proper
     * ErrorResponse.
     */
    @Test
    void shouldFailToCreateItem_WhenNoPermission() {
        FileSystemItemEntity parentFolder = createAFolder(UUID.randomUUID());
        fileSystemItemRepository.save(parentFolder);
        
        userSnapshotRepository.save(
            new UserSnapshotEntity(ownerId, "valid-mail-2@domain.com")
        );
        
        userSnapshotRepository.save(
            new UserSnapshotEntity(parentFolder.getOwner(), "valid-mail-3@domain.com")
        );
        
        String requestBody = """
            {
                "name": "unauthorized_file.txt",
                "size": 5678,
                "mimeType": "text/plain",
                "status": "UPLOADED",
                "type": "FILE",
                "chunks": null,
                "s3Url": "https://s3.bucket.com/unauthorized_file.txt",
                "parentId": "%s"
            }
            """.formatted(parentFolder.getId());
        
        requestSpecification()
            .header(CustomHttpHeader.USER_ID.getValue(), ownerId)
            .body(requestBody)
            .when()
            .post("/create")
            .then()
            .statusCode(HttpStatus.FORBIDDEN.value())
            .body("status", equalTo(HttpStatus.FORBIDDEN.value()))
            .body("error", equalTo("Forbidden"))
            .body("errorCode", equalTo("PERM-01"))
            .body("message", equalTo("You do not have permission to access this resource."))
            .body("details", containsString("You do not have enough permission"))
            .body("path", equalTo("/create"))
            .body("errorId", notNullValue())
            .body("timestamp", notNullValue());
    }
    
    /**
     * When the parent item is not found, the request should fail with a proper ErrorResponse.
     */
    @Test
    void shouldFailToCreateItem_WhenParentNotFound() {
        String nonExistingParentId = UUID.randomUUID().toString();
        
        userSnapshotRepository.save(
            new UserSnapshotEntity(ownerId, "valid-mail-4@domain.com")
        );
        
        String requestBody = """
            {
                "name": "file_with_missing_parent.txt",
                "size": 91011,
                "mimeType": "text/plain",
                "status": "UPLOADED",
                "type": "FILE",
                "chunks": null,
                "s3Url": "https://s3.bucket.com/file_with_missing_parent.txt",
                "parentId": "%s"
            }
            """.formatted(nonExistingParentId);
        
        requestSpecification()
            .header(CustomHttpHeader.USER_ID.getValue(), ownerId)
            .body(requestBody)
            .when()
            .post("/create")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("status", equalTo(HttpStatus.NOT_FOUND.value()))
            .body("error", equalTo("Not Found"))
            .body("errorCode", equalTo("ITEM-01"))
            .body("message", equalTo("Item not found."))
            .body("details", containsString("Parent item not found"))
            .body("path", equalTo("/create"))
            .body("errorId", notNullValue())
            .body("timestamp", notNullValue());
    }
    
    /**
     * When the user ID is not found in the snapshot, the request should fail with a proper
     * ErrorResponse indicating user not found.
     */
    @Test
    void shouldFailToCreateItem_WhenUserNotFoundInSnapshot() {
        FileSystemItemEntity parentFolder = createAFolder(UUID.randomUUID());
        fileSystemItemRepository.save(parentFolder);
        
        UUID nonExistingUserId = UUID.randomUUID();
        
        String requestBody = """
            {
                "name": "file_by_non_existing_user.txt",
                "size": 101112,
                "mimeType": "text/plain",
                "status": "UPLOADED",
                "type": "FILE",
                "chunks": null,
                "s3Url": "https://s3.bucket.com/file_by_non_existing_user.txt",
                "parentId": "%s"
            }
            """.formatted(parentFolder.getId());
        
        requestSpecification()
            .header(CustomHttpHeader.USER_ID.getValue(), nonExistingUserId)
            .body(requestBody)
            .when()
            .post("/create")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .body("status", equalTo(HttpStatus.NOT_FOUND.value()))
            .body("error", equalTo("Not Found"))
            .body("errorCode", equalTo("USER-01"))
            .body("message", equalTo("User not found."))
            .body("details", containsString("User not found"))
            .body("path", equalTo("/create"))
            .body("errorId", notNullValue())
            .body("timestamp", notNullValue());
    }
    
    private FileSystemItemEntity createAFolder(UUID ownerId) {
        return FileSystemItemEntity.builder()
            .name("ParentFolder")
            .owner(ownerId)
            .status(UploadStatus.UPLOADED)
            .type(FileSystemItemType.FOLDER)
            .build();
    }
}
