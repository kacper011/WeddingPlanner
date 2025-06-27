package com.kacper.wedding_planner.service;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

public class EmailServiceTest {

    private JavaMailSender mailSender;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        emailService = new EmailService(mailSender);
    }

    @Test
    void shouldSendWelcomeEmailSuccessfully() throws Exception {
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendWelcomeEmail("user@example.com", "Kacper");

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void shouldSendReminderEmailSuccessfully() throws Exception{
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendReminderEmail("user@example.com", "Kacper", "Wesele", LocalDate.of(2025, 6, 29));//Trzeba ustawiać datę na 2 dni przed

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
}

