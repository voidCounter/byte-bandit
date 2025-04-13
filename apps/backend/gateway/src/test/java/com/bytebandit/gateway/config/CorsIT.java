package com.bytebandit.gateway.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bytebandit.gateway.configurer.AbstractPostgresContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CorsIT extends AbstractPostgresContainer {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Test CORS configuration by sending an OPTIONS request to the endpoint with a specific allowed
     * origin.
     */
    @Test
    void whenRequestFromAllowedOrigin_thenSucceeds() throws Exception {
        mockMvc.perform(options("/api/v1/auth/csrf")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET"))
            .andExpect(status().isOk())
            .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    /**
     * Test CORS configuration by sending an OPTIONS request to the endpoint with a disallowed.
     */
    @Test
    void whenRequestFromDisallowedOrigin_thenIsBlocked() throws Exception {
        mockMvc.perform(options("/api/v1/auth/csrf")
                .header("Origin", "http://malicious.com")
                .header("Access-Control-Request-Method", "GET"))
            .andExpect(status().isForbidden());
    }
}
