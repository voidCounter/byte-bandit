package com.bytebandit.syncservice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SyncServiceApplicationIT {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Test to check if the Spring application context loads successfully.
     */
    @Test
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
    }
}
