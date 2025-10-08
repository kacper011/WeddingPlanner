package com.kacper.wedding_planner.service;

import com.kacper.wedding_planner.exception.EmailSendException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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
            helper.setSubject("Welcome to Wedding Planner!");
            helper.setText("<h2>Hello " + username + "!</h2><p>Thank you for registering in our wedding planner 🎉</p>", true);
            helper.setFrom("weddingplannerwelcome@gmail.com");

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send welcome email", e);
        }
    }

    public void sendReminderEmail(String to, String username, String eventTitle, LocalDate eventDate) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Event reminder: " + eventTitle);
            helper.setText("<p>Hello " + username + "!</p>" +
                            "<p>This is a reminder about your event <strong>" + eventTitle + "</strong> scheduled for <strong>" + eventDate + "</strong>.</p>",
                    true);
            helper.setFrom("weddingplannerwelcome@gmail.com");

            System.out.println("📧 Sending reminder email to: " + to + " (event: " + eventTitle + ", date: " + eventDate + ")");
            mailSender.send(message);
            System.out.println("✅ Email sent successfully!");
        } catch (MessagingException e) {
            System.out.println("❌ Error sending email to: " + to);
            throw new EmailSendException("Failed to send reminder email", e);
        }
    }
}
