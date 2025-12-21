package com.kacper.wedding_planner.unit.service;

import com.kacper.wedding_planner.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class EmailServiceTest {

    private JavaMailSender mailSender;
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        emailService = new EmailService(mailSender, "test@example.com");
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

    @Test
    void shouldThrowExceptionIfWelcomeEmailFails() throws MessagingException {
        MimeMessage mockMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mockMessage);

        doThrow(new RuntimeException(new MessagingException("Symulowana awaria"))).when(mailSender).send(any(MimeMessage.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                emailService.sendWelcomeEmail("user@example.com", "Kacper")
        );

        System.out.println("Exception message: " + exception.getMessage());
        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof MessagingException);
    }

    @Test
    void shouldThrowExceptionIfReminderEmailFails() throws MessagingException {

        MimeMessage mockMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mockMessage);

        doThrow(new RuntimeException(new MessagingException("Symulowana awaria")))
                .when(mailSender).send(any(MimeMessage.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                emailService.sendReminderEmail("user@example.com", "Kacper", "Wesele", LocalDate.now())
        );

        System.out.println("Exception message: " + exception.getMessage());

        assertNotNull(exception.getCause());
        assertTrue(exception.getCause() instanceof MessagingException);
    }

}

