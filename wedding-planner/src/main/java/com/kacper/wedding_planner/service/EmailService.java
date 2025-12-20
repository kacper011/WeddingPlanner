package com.kacper.wedding_planner.service;

import com.kacper.wedding_planner.exception.EmailSendException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDate;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public EmailService(JavaMailSender mailSender,
                        @Value("${app.mail.from}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    public void sendWelcomeEmail(String to, String username) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setFrom(fromAddress);
            helper.setSubject("Witamy w Wedding Planner 🎉");

            String safeUsername = HtmlUtils.htmlEscape(username);

            helper.setText("""
                <h2>Witaj %s!</h2>
                <p>Dziękujemy za rejestrację w Wedding Planner 💍</p>
                """.formatted(safeUsername), true);

            mailSender.send(message);

            log.info("Welcome email sent to {}", to);

        } catch (MessagingException e) {
            log.error("Failed to send welcome email to {}", to, e);
        }
    }

    public void sendReminderEmail(String to, String username,
                                  String eventTitle, LocalDate eventDate) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setFrom(fromAddress);
            helper.setSubject("Przypomnienie: " + eventTitle);

            helper.setText("""
                <p>Witaj %s!</p>
                <p>Przypominamy o wydarzeniu <strong>%s</strong>
                zaplanowanym na <strong>%s</strong>.</p>
                """.formatted(
                    HtmlUtils.htmlEscape(username),
                    HtmlUtils.htmlEscape(eventTitle),
                    eventDate
            ), true);

            mailSender.send(message);
            log.info("Reminder email sent to {}", to);

        } catch (MessagingException e) {
            log.error("Failed to send reminder email to {}", to, e);
            throw new EmailSendException("Nie udało się wysłać emaila", e);
        }
    }
}
