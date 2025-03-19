package com.bytebandit.userservice.service;

import com.bytebandit.userservice.enums.EmailTemplate;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.spring6.SpringTemplateEngine;


import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


class RegistrationEmailServiceIntegrationTest {

    @RegisterExtension
    static GreenMailExtension greenMailExtension = new GreenMailExtension(ServerSetupTest.SMTP_IMAP)
            .withConfiguration(GreenMailConfiguration
                    .aConfig()
                    .withUser(
                            "test-user@localhost",
                            "test-user",
                            "test-user-pwd"
                    ))
                    .withPerMethodLifecycle(false);

    @Test
    void sendEmail_ShouldActuallySendEmailViaSmtp() throws MessagingException {
        String recipient = "user@example.com";
        String subject = "Welcome!";
        String body = "Welcome to our platform!";
        EmailTemplate emailTemplate = mock(EmailTemplate.class);
        when(emailTemplate.getTemplatePath()).thenReturn("template.html");

        Map<String, Object> templateModel = Map.of("name", "John");
        SpringTemplateEngine templateEngine = mock(SpringTemplateEngine.class);
        when(templateEngine.process(eq("template.html"), any())).thenReturn(body);
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("localhost");
        mailSender.setPort(ServerSetupTest.SMTP.getPort()); // GreenMail SMTP port
        mailSender.setUsername("");
        mailSender.setPassword("");

        RegistrationEmailService registrationEmailService = new RegistrationEmailService(templateEngine, mailSender);
        registrationEmailService.sendEmail(recipient, subject, emailTemplate, templateModel);
        greenMailExtension.waitForIncomingEmail(1);
        MimeMessage[] receivedMessages = greenMailExtension.getReceivedMessages();

        assertThat(receivedMessages).hasSize(1);
        MimeMessage receivedMessage = receivedMessages[0];
        assertThat(receivedMessage.getAllRecipients()[0]).hasToString(recipient);
        assertThat(receivedMessage.getSubject()).isEqualTo(subject);
        verify(templateEngine).process(eq("template.html"), any());
    }
}
