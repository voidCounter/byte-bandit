package com.bytebandit.userservice.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

import java.util.UUID;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.spring6.SpringTemplateEngine;

@ActiveProfiles("test")
public class RegistrationEmailServiceTest {

    /**
     * Tests the {@code confirmationUrl} method of {@link RegistrationEmailService} to ensure it
     * generates the correct confirmation URL using the provided token and user ID.
     *
     * <p>This test verifies that the {@code confirmationUrl} method properly constructs the URL
     * by concatenating the backend host, API prefix, and query parameters (token and userId). The
     * generated URL is compared with the expected URL format to ensure correctness.</p>
     *
     * <p>Dependencies:</p>
     * <ul>
     *     <li>{@link SpringTemplateEngine} and {@link JavaMailSenderImpl} are mocked, as they
     *     are not relevant to this test.</li>
     *     <li>{@link ReflectionTestUtils} is used to inject the values for {@code backendHost}
     *     and {@code apiPrefix}
     *         and to invoke the private {@code confirmationUrl} method for testing.</li>
     * </ul>
     *
     * @throws Exception if there is an issue invoking the method or performing assertions
     */
    @Test
    public void confirmationUrl_ShouldGenerateCorrectUrl() {
        String backendHost = "http://localhost:8080";
        String apiPrefix = "/api";
        String token = "abc123";
        UUID userId = UUID.randomUUID();

        SpringTemplateEngine templateEngine = mock(SpringTemplateEngine.class);
        JavaMailSenderImpl mailSender = mock(JavaMailSenderImpl.class);

        RegistrationEmailService service = new RegistrationEmailService(templateEngine, mailSender);
        ReflectionTestUtils.setField(service, "backendHost", backendHost);
        ReflectionTestUtils.setField(service, "apiPrefix", apiPrefix);

        String confirmationUrl = ReflectionTestUtils.invokeMethod(
            service,
            "confirmationUrl",
            token,
            userId
        );

        assertThat(confirmationUrl).isEqualTo(
            String.format("%s%s/verify?token=%s&userid=%s", backendHost, apiPrefix, token, userId)
        );
    }
}
