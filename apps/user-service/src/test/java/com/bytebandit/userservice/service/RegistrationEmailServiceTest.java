package com.bytebandit.userservice.service;

import com.bytebandit.userservice.enums.EmailTemplate;
import com.bytebandit.userservice.exception.ErrorSendingEmailException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationEmailServiceTest {

    @Mock
    private SpringTemplateEngine springTemplateEngine;

    @Mock
    private JavaMailSender javaMailSender;

    @Spy
    @InjectMocks
    private RegistrationEmailService registrationEmailServiceSpy;

    @Test
    void sendEmail_ShouldProcessTemplateAndSendEmail() {
        String sendEmailTo = "user@example.com";
        String emailSubject = "Welcome!";
        String emailBody = "<html>Email Content</html>";

        EmailTemplate emailTemplate = mock(EmailTemplate.class);
        when(emailTemplate.getTemplatePath()).thenReturn("template.html");
        Map<String, Object> templateModel = Map.of("name", "John");
        when(springTemplateEngine.process(eq(
                "template.html"),
                any(Context.class))).thenReturn(emailBody);

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        registrationEmailServiceSpy.sendEmail(sendEmailTo, emailSubject, emailTemplate, templateModel);
        verify(springTemplateEngine).process(eq("template.html"), any(Context.class));
        verify(registrationEmailServiceSpy).sendMailMessage(javaMailSender, sendEmailTo, emailSubject, emailBody);
    }

    @Test
    void sendEmail_ShouldThrowException_WhenTemplateEngineFailsToProcess() {
        String sendEmailTo = "user@example.com";
        String emailSubject = "Welcome!";
        EmailTemplate emailTemplate = mock(EmailTemplate.class);
        Map<String, Object> templateModel = Map.of("name", "John");

        when(springTemplateEngine.process(eq(emailTemplate.getTemplatePath()), any(Context.class)))
                .thenThrow(new ErrorSendingEmailException(
                        "Template processing failed",
                        new RuntimeException()
                ));

        assertThrows(ErrorSendingEmailException.class, () ->
                registrationEmailServiceSpy.sendEmail(
                        sendEmailTo,
                        emailSubject,
                        emailTemplate,
                        templateModel
                ));
    }
}
