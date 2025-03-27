package com.bytebandit.userservice.service;

import com.bytebandit.userservice.exception.ErrorSendingEmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.UUID;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

public abstract class EmailService {

    /**
     * Sends an email message using the provided JavaMailSender.
     *
     * @param mailSender   the JavaMailSender to use for sending the email.
     * @param sendEmailTo  the recipient's email address.
     * @param emailSubject the subject of the email.
     * @param emailBody    the body of the email, which can be HTML formatted.
     */
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
        String token,
        UUID userId
    );
}
