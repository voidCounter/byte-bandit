package com.bytebandit.gateway.filter;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.cloud.config.discovery.enabled=false",
    "spring.cloud.config.enabled=false",
    "eureka.client.enabled=false"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CsrfFilterIT {
    
    @LocalServerPort
    int port;
    
    @BeforeAll
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }
    
    /**
     * Test to check if the CSRF cookie is set correctly when a GET request is made to the
     * "/api/v1/auth/csrf" endpoint.
     */
    @Test
    void testGetRequestSetsCsrfCookie() {
        Response response = RestAssured
            .given()
            .when()
            .get("/api/v1/auth/csrf")
            .then()
            .statusCode(200)
            .cookie("XSRF-TOKEN")
            .extract().response();
        
        String csrfToken = response.getCookie("XSRF-TOKEN");
        Assertions.assertNotNull(csrfToken, "XSRF-TOKEN should be present");
    }
    
    /**
     * Test to check if the CSRF token is validated correctly when a POST request is made to the
     * "/api/v1/auth/test-csrf" endpoint.
     */
    @Test
    void testPostWithCsrfToken() {
        Response response = RestAssured
            .given()
            .when()
            .get("/api/v1/auth/csrf")
            .then()
            .statusCode(200)
            .extract().response();
        
        String csrfToken = response.getCookie("XSRF-TOKEN");
        String sessionCookie = response.getCookie("JSESSIONID");
        
        RestAssured
            .given()
            .cookie("XSRF-TOKEN", csrfToken)
            .cookie("JSESSIONID", sessionCookie)
            .header("X-XSRF-TOKEN", csrfToken)
            .contentType("application/json")
            .body("{}")
            .when()
            .post("/api/v1/auth/test-csrf")
            .then()
            .statusCode(200);
    }
}
