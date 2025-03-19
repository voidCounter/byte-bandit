package com.bytebandit.userservice;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.mail.MailSenderValidatorAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
//@ActiveProfiles("test")
class UserServiceApplicationTests {

    private static GreenMail greenMail;

    @BeforeAll
    static void setUp() {
        greenMail = new GreenMail(new ServerSetup(3025, null, "smtp"));
        greenMail.start();
        greenMail.setUser("foo", "foo-pwd");
    }

    @AfterAll
    static void stopGreenMail() {
        greenMail.stop();
    }

    @Test
    void contextLoads() {
    }
}
