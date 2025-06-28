package com.kacper.wedding_planner.service;

import com.kacper.wedding_planner.repository.EventRepository;
import com.kacper.wedding_planner.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

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
}
