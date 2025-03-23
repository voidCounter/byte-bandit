package com.bytebandit.userservice.service;

import com.bytebandit.userservice.enums.EmailTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationEmailService extends EmailService {

    private final SpringTemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;

    @Value("${api.host.uri}")
    private String backendHost;

    @Value("${api.prefix}")
    private String apiPrefix;

    @Override
    public void sendEmail(
            String sendEmailTo,
            String fullName,
            String token,
            UUID userId
    ) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("confirmationUrl", confirmationUrl(token, userId));
        templateModel.put("recipientName", fullName);

        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        String emailBody = templateEngine.process(
                EmailTemplate.REGISTRATION_CONFIRMATION.getTemplatePath(),
                thymeleafContext
        );
        sendMailMessage(
                javaMailSender,
                sendEmailTo,
                "REGISTRATION CONFIRMATION",
                emailBody
        );
    }

    private String confirmationUrl(String token, UUID userId) {
        return UriComponentsBuilder.fromUriString(backendHost + apiPrefix + "/verify")
                .queryParam("token", token)
                .queryParam("userid", userId)
                .build()
                .toUriString();
    }
}
