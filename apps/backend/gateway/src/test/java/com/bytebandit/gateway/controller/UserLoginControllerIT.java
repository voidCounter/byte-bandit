package com.bytebandit.gateway.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import com.bytebandit.gateway.configurer.AbstractPostgresContainer;
import com.bytebandit.gateway.model.UserEntity;
import com.bytebandit.gateway.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@Import({BCryptPasswordEncoder.class})
@Slf4j
class UserLoginControllerIT extends AbstractPostgresContainer {

    @LocalServerPort
    private int port;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    private final String requestPath = "/api/v1/auth/login";
    private final UUID userId = UUID.randomUUID();
    private String csrfToken;

    /**
     * This method sets up the test environment by deleting all existing users and creating a new
     * user with a known password.
     */
    @BeforeEach
    void setup() {
        RestAssured.port = port;

        Response response = RestAssured
            .given()
            .when()
            .get("/api/v1/auth/csrf")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .response();
        log.info("{}", response);

        csrfToken = response.getCookies().get("XSRF-TOKEN");

        userRepository.deleteAll();

        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setPasswordHash(
            passwordEncoder.encode("ValidPass$1")
        );
        user.setVerified(true);

        userRepository.save(user);
    }

    private RequestSpecification requestSpecification() {
        log.info(csrfToken);
        return RestAssured.given()
            .contentType(ContentType.JSON)
            .header("X-XSRF-TOKEN", csrfToken)
            .cookie("XSRF-TOKEN", csrfToken);
    }

    /**
     * This test verifies that a user can log in with valid credentials. It sends a POST request to
     * the login endpoint with the user's email and password, and checks that the response status
     * code is 200 (OK) and the response body contains the expected values.
     */
    @Test
    void login_shouldSucceedWithValidCredentials() {

        String jsonBody = """
                {
                    "email": "test@example.com",
                    "password": "ValidPass$1",
                    "userId": "..."
                }
            """;

        requestSpecification()
            .body(jsonBody)
            .when()
            .post(requestPath)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("status", equalTo(200))
            .body("message", equalTo("Login successful"))
            .body("data", equalTo(true))
            .body("timestamp", notNullValue())
            .body("path", equalTo("/api/v1/auth/login"));
    }

    /**
     * This test verifies that a user cannot log in with an invalid email. It sends a POST request
     * to the login endpoint with an invalid email and a valid password, and checks that the
     * response status code is 401 (Unauthorized) and the response body contains the expected error
     * message.
     */
    @Test
    void login_shouldFailWithInvalidPassword() {

        String jsonBody = """
               {
                   "email": "test@example.com",
                   "password": "ValidPass1$",
                   "userId": "..."
               }
            """;

        requestSpecification()
            .body(jsonBody)
            .when()
            .post(requestPath)
            .then()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
            .body("status", equalTo(401))
            .body("errorCode", equalTo("AUTH-01"))
            .body("message", containsString("Bad credentials"))
            .body("path", equalTo("/api/v1/auth/login"));
    }
}
