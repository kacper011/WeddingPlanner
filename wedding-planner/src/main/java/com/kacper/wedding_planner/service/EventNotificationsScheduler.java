package com.kacper.wedding_planner.service;

import com.kacper.wedding_planner.model.Event;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.EventRepository;
import com.kacper.wedding_planner.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class EventNotificationsScheduler {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public EventNotificationsScheduler(EventRepository eventRepository, UserRepository userRepository, EmailService emailService) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @PostConstruct
    public void onStartupCheck() {
        sendReminders();
    }

    @Scheduled(cron = "0 * * * * *") // codziennie o 8:00
    public void sendReminders() {
        LocalDate targetDate = LocalDate.now().plusDays(2);
        System.out.println("🔄 Sprawdzam wydarzenia na dzień: " + targetDate);

        List<Event> events = eventRepository.findByDateAndReminderSentFalse(targetDate);

        if (events.isEmpty()) {
            System.out.println("ℹ️ Brak wydarzeń do przypomnienia na dzień: " + targetDate);
        }

        for (Event event : events) {
            User user = event.getUser();
            if (user != null && user.getEmail() != null) {
                emailService.sendReminderEmail(
                        user.getEmail(),
                        user.getFirstName(),
                        event.getTitle(),
                        event.getDate()
                );

                event.setReminderSent(true);
                eventRepository.save(event);
            }
        }
    }
}
