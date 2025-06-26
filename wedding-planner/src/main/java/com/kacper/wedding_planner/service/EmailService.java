package com.kacper.wedding_planner.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendWelcomeEmail(String to, String username) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Witamy w Wedding Planner!");
            helper.setText("<h2>Cześć " + username + "!</h2><p>Dziękujemy za rejestrację w naszym planerze wesela 🎉</p>", true);
            helper.setFrom("weddingplannerwelcome@gmail.com");

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Nie udało się wysłać maila powitalnego", e);
        }
    }

    public void sendReminderEmail(String to, String username, String eventTitle, LocalDate eventDate) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Przypomnienie o wydarzeniu: " + eventTitle);
            helper.setText("<p>Cześć " + username + "!</p>" +
                            "<p>Przypominamy o wydarzeniu <strong>" + eventTitle + "</strong> zaplanowanym na <strong>" + eventDate + "</strong>.</p>",
                    true);
            helper.setFrom("weddingplannerwelcome@gmail.com");

            System.out.println("📧 Wysyłam przypomnienie na e-mail: " + to + " (wydarzenie: " + eventTitle + ", data: " + eventDate + ")");
            mailSender.send(message);
            System.out.println("✅ E-mail został wysłany pomyślnie!");
        } catch (MessagingException e) {
            System.out.println("❌ Błąd przy wysyłaniu maila do: " + to);
            throw new RuntimeException("Nie udało się wysłać maila przypominającego", e);
        }
    }
}
