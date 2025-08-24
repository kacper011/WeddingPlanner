package com.kacper.wedding_planner.unit.service;

import com.kacper.wedding_planner.model.Event;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.EventRepository;
import com.kacper.wedding_planner.repository.UserRepository;
import com.kacper.wedding_planner.service.EmailService;
import com.kacper.wedding_planner.service.EventNotificationsScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EventNotificationsSchedulerTest {

    private EmailService emailService;
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private EventNotificationsScheduler scheduler;

    @BeforeEach
    void setUp() {
        eventRepository = mock(EventRepository.class);
        userRepository = mock(UserRepository.class);
        emailService = mock(EmailService.class);
        scheduler = new EventNotificationsScheduler(eventRepository, userRepository, emailService);
    }

    @Test
    void shouldNotSendEmailsWhenNoEventsFound() {
        when(eventRepository.findByDateAndReminderSentFalse(any())).thenReturn(Collections.emptyList());

        scheduler.sendReminders();

        verify(emailService, never()).sendReminderEmail(any(), any(), any(), any());
        verify(eventRepository, never()).save(any());
    }

    @Test
    void shouldSendReminderAndMarkEventAsSent() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("Kacper");

        Event event = new Event();
        event.setUser(user);
        event.setTitle("Wesele");
        event.setDate(LocalDate.now().plusDays(2));
        event.setReminderSent(false);

        when(eventRepository.findByDateAndReminderSentFalse(any())).thenReturn(List.of(event));

        scheduler.sendReminders();

        verify(emailService).sendReminderEmail("test@example.com", "Kacper", "Wesele", event.getDate());
        verify(eventRepository).save(event);
        assertTrue(event.isReminderSent());
    }
}
