package com.bytebandit.userservice.service;

import com.bytebandit.userservice.enums.EmailTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RegistrationEmailService extends EmailService {

    private final SpringTemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(
            String sendEmailTo,
            String emailSubject,
            EmailTemplate emailTemplate,
            Map<String, Object> templateModel
    ) {

        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        String emailBody = templateEngine.process(
                emailTemplate.getTemplatePath(),
                thymeleafContext
        );
        sendMailMessage(javaMailSender, sendEmailTo, emailSubject, emailBody);
    }
}
