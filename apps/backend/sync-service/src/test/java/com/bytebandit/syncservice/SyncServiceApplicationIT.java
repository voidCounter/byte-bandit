package com.bytebandit.syncservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SyncServiceApplicationIT {

    /**
     * Test to check if the Spring application context loads successfully.
     */
    @Test
    void contextLoads() {
        // This test will fail if the application context cannot start.
    }

}
