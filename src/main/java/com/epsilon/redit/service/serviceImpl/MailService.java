package com.epsilon.redit.service.serviceImpl;

import com.epsilon.redit.exception.SpringRedittException;
import com.epsilon.redit.model.NotificationEmail;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.InternetAddress;

@Service
@AllArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender javaMailSender;

    private final MailContentBuilder mailContentBuilder;

    @Async
    public void sendMail(NotificationEmail notificationEmail){
        MimeMessagePreparator mimeMessagePreparator = mimeMessage -> {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            mimeMessageHelper.setFrom(new InternetAddress("no-reply@springreditt.com", "Spring Reditt"));
            mimeMessageHelper.setReplyTo("no-reply@springreditt.com");
            mimeMessageHelper.setTo(notificationEmail.getRecipient());
            mimeMessageHelper.setSubject(notificationEmail.getSubject());
            mimeMessageHelper.setText(mailContentBuilder.build(notificationEmail.getBody()), true);
        };

        try {
            javaMailSender.send(mimeMessagePreparator);
            log.info("Mail sent!");
        }catch (MailException e){
            throw new SpringRedittException(String.format("Exception occurred when sending mail to %s", notificationEmail.getRecipient()));
        }
    }
}
