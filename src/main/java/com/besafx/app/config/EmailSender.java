package com.besafx.app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailSender {

    private final Logger logger = LoggerFactory.getLogger(EmailSender.class);

    @Autowired
    private JavaMailSender sender;

    public void send(String title, String content, String... toEmailList) {
        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("anni4ksa@gmail.com");
            helper.setTo(toEmailList);
            helper.setSubject(title);
            helper.setText(content, true);
            sender.send(message);
        } catch (MessagingException e) {
            logger.error("خطأ فى إرسال البريد الإلكتروني", e);
            throw new CustomException(e.getMessage());
        }
    }
}
