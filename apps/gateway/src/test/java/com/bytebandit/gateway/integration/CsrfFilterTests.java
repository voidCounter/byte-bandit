package com.bytebandit.gateway.integration;

import java.util.Objects;

import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CsrfFilterTests {
    @Autowired
    private MockMvc mockMvc;

    /**
     * Verifies that a GET request to the "/csrf" endpoint sets the CSRF cookie named "XSRF-TOKEN".
     * This test ensures that a proper CSRF token is generated and included as a cookie in the response
     * when accessing the relevant endpoint. It asserts that the response status is OK and the cookie
     * "XSRF-TOKEN" exists in the response.
     * @throws Exception if an error occurs while performing the request or asserting the results.
     */
    @Test
    public void testGetRequestSetsCsrfCookie() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/csrf"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.cookie().exists("XSRF-TOKEN"));
    }

    /**
     * Verifies that a POST request to the "/test-csrf" endpoint without a CSRF token
     * results in a forbidden response.
     * This test ensures that the server denies access to POST requests that do not
     * include the necessary CSRF token.
     * @throws Exception if an error occurs while performing the request or asserting the results.
     */
    @Test
    public void testPostWithoutCsrfToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/test-csrf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    /**
     * Verifies that a POST request to the "/test-csrf" endpoint succeeds when a valid CSRF token
     * is included in the request.
     * Steps:
     * <li> Perform a GET request to the "/csrf" endpoint to obtain a CSRF token from the response
     * cookie.</li>
     * <li>Extract the CSRF token value from the "XSRF-TOKEN" cookie in the response.</li>
     * <li>Perform a POST request to the "/test-csrf" endpoint, including the CSRF token as a header
     *    (X-XSRF-TOKEN) and as a cookie.</li>
     * <li>Assert that the response status is OK, indicating that the CSRF validation was
     * successful.</li>
     *
     * @throws Exception if an error occurs while performing the requests or asserting the results.
     */
    @Test
    public void testPostWithCsrfToken() throws Exception {
        var getResponse = mockMvc.perform(MockMvcRequestBuilders.get("/csrf")).andReturn();
        String csrfToken = Objects.requireNonNull(getResponse.getResponse().getCookie("XSRF-TOKEN")).getValue();

        mockMvc.perform(MockMvcRequestBuilders.post("/test-csrf")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-XSRF-TOKEN", csrfToken)
                        .cookie(getResponse.getResponse().getCookie("XSRF-TOKEN"))
                        .content("{}"))
                .andExpect(status().isOk());
    }
}
