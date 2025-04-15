package com.bytebandit.userservice.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import com.bytebandit.userservice.configurer.AbstractPostgresContainer;
import com.bytebandit.userservice.model.UserEntity;
import com.bytebandit.userservice.repository.UserRepository;
import com.bytebandit.userservice.service.RegistrationEmailService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@Slf4j
class UserRegistrationControllerIT extends AbstractPostgresContainer {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private RegistrationEmailService registrationEmailService;

    private final String requestPath = "/register";

    /**
     * Setup.
     */
    @BeforeEach
    void setup() {
        RestAssured.port = port;
        userRepository.deleteAll();

    }

    private RequestSpecification requestSpecification() {
        return RestAssured.given()
            .contentType(ContentType.JSON);
    }

    /**
     * Test for invalid user registration with missing fields.
     */
    @Test
    void register_shouldSucceedWithValidData() {
        String requestBody = """
                {
                    "fullName": "Test User",
                    "email": "newuser@example.com",
                    "password": "ValidPass$1"
                }
                """;

        requestSpecification()
            .body(requestBody)
            .when()
            .post(requestPath)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body(equalTo("User registered successfully"));
    }

    /**
     * Test if duplicate email fails registration.
     */
    @Test
    void register_shouldFailIfEmailAlreadyExists() {
        UserEntity user = new UserEntity();
        user.setEmail("duplicate@example.com");
        user.setPasswordHash("PasswordHash#1");
        user.setVerified(true);
        userRepository.save(user);

        String requestBody = """
                {
                    "fullName": "Test User",
                    "email": "duplicate@example.com",
                    "password": "ValidPass$1"
                }
                """;

        requestSpecification()
            .body(requestBody)
            .when()
            .post(requestPath)
            .then()
            .statusCode(HttpStatus.CONFLICT.value())
            .body("status", equalTo(409))
            .body("error", equalTo("Conflict"))
            .body("errorCode", equalTo("USER-02"))
            .body("message", equalTo("User already exists."))
            .body("details", containsString("User with provided email already exists."))
            .body("path", equalTo(requestPath))
            .body("timestamp", notNullValue());
    }

    /**
     * Test if invalid email format fails registration.
     */
    @Test
    void register_shouldFailWithInvalidEmailFormat() {
        String requestBody = """
            {
                "fullName": "Test User",
                "email": "not-an-email",
                "password": "ValidPass$1"
            }
            """;

        requestSpecification()
            .body(requestBody)
            .when()
            .post(requestPath)
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("status", equalTo(400))
            .body("error", equalTo("Bad Request"))
            .body("errorCode", equalTo("SEC-01"))
            .body("message", containsString("Invalid email address provided."))
            .body("path", equalTo(requestPath))
            .body("timestamp", notNullValue());
    }

    /**
     * Test if password is null fails registration.
     */
    @Test
    void register_shouldFailWithWeakPassword() {
        String requestBody = """
            {
                "fullName": "Test User",
                "email": "user@example.com",
                "password": "weak-password"
            }
            """;

        requestSpecification()
            .body(requestBody)
            .when()
            .post(requestPath)
            .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("status", equalTo(400))
            .body("error", equalTo("Bad Request"))
            .body("errorCode", equalTo("SEC-04"))
            .body("message",
                containsString("Password must be at least 8 characters."))
            .body("path", equalTo(requestPath))
            .body("timestamp", notNullValue());
    }

}
