package com.bytebandit.userservice.service;

import com.bytebandit.userservice.enums.EmailTemplate;
import com.bytebandit.userservice.exception.ErrorSendingEmailException;
import jakarta.mail.MessagingException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;

import java.util.Map;

public abstract class EmailService {

    protected void sendMailMessage(
            JavaMailSender mailSender,
            String sendEmailTo,
            String emailSubject,
            String emailBody
    ) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(sendEmailTo);
            helper.setSubject(emailSubject);
            helper.setText(emailBody, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new ErrorSendingEmailException("Error sending email", e);
        }
    }

    public abstract void sendEmail(
        String sendEmailTo,
        String fullName,
        String token
    ) ;
}
