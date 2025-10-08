package com.kacper.wedding_planner.service;

import com.kacper.wedding_planner.model.Event;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.EventRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class EventNotificationsScheduler {

    private static final Logger log = LoggerFactory.getLogger(EventNotificationsScheduler.class);
    private final EventRepository eventRepository;
    private final EmailService emailService;

    public EventNotificationsScheduler(EventRepository eventRepository, EmailService emailService) {
        this.eventRepository = eventRepository;
        this.emailService = emailService;
    }

    @PostConstruct
    public void onStartupCheck() {
        sendReminders();
    }

    @Transactional
    @Scheduled(cron = "0 * * * * *") // codziennie o 8:00
    public void sendReminders() {
        LocalDate targetDate = LocalDate.now().plusDays(2);
        log.info("🔄 Sprawdzam wydarzenia na dzień: " + targetDate);

        List<Event> events = eventRepository.findByDateAndReminderSentFalse(targetDate);

        if (events.isEmpty()) {
            System.out.println("ℹ️ Brak wydarzeń do przypomnienia na dzień: " + targetDate);
            return;
        }

        for (Event event : events) {
            try {
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

                    log.info("✅ Wysłano przypomnienie dla użytkownika {} o wydarzeniu '{}'", user.getEmail(), event.getTitle());
                }
            } catch (Exception e) {
                log.error("❌ Błąd przy wysyłaniu przypomnienia dla wydarzenia ID: {}", event.getId(), e);
            }
        }
    }
}
