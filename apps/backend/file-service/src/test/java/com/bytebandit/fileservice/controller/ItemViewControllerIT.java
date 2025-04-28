package com.bytebandit.fileservice.controller;

import static org.hamcrest.Matchers.equalTo;

import com.bytebandit.fileservice.configurer.AbstractPostgresContainer;
import com.bytebandit.fileservice.enums.FileSystemItemType;
import com.bytebandit.fileservice.enums.UploadStatus;
import com.bytebandit.fileservice.model.FileSystemItemEntity;
import com.bytebandit.fileservice.model.UserSnapshotEntity;
import com.bytebandit.fileservice.repository.FileSystemItemRepository;
import com.bytebandit.fileservice.repository.UserSnapshotRepository;
import com.bytebandit.fileservice.service.RoleBasedAccessControlService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.util.UUID;
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

    @MockitoBean
    private RoleBasedAccessControlService roleBasedAccessControlService;

    private final UUID ownerId = UUID.randomUUID();

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
     * This test verifies that the item view request is handled successfully when the user has
     * access to the item.
     */
    @Test
    void shouldReturnItemViewSuccessfully_WhenUserHasAccess() {
        userSnapshotRepository.save(
            new UserSnapshotEntity(ownerId, "owner-1@example.com")
        );

        FileSystemItemEntity fileItem = createAFile(ownerId);
        fileSystemItemRepository.save(fileItem);

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
            .body("data.parentId", equalTo(fileItem.getParent().getId() != null
                ? fileItem.getParent().getId().toString() : null));
    }

    private FileSystemItemEntity createAFile(UUID ownerId) {
        return FileSystemItemEntity.builder()
            .name("test_file.txt")
            .size(1234L)
            .mimeType("text/plain")
            .status(UploadStatus.UPLOADED)
            .type(FileSystemItemType.FILE)
            .s3Url("https://s3.bucket.com/test_file.txt")
            .owner(ownerId)
            .build();
    }
}
