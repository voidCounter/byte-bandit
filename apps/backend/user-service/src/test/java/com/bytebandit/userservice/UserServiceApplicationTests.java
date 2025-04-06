package com.bytebandit.userservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {UserServiceApplication.class})
@ActiveProfiles("test")
class UserServiceApplicationTests {
    @Test
    void contextLoads() {
    }
}
