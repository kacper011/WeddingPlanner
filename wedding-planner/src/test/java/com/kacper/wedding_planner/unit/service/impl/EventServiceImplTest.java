package com.kacper.wedding_planner.unit.service.impl;

import com.kacper.wedding_planner.dto.EventDTO;
import com.kacper.wedding_planner.exception.UserNotFoundException;
import com.kacper.wedding_planner.model.Event;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.EventRepository;
import com.kacper.wedding_planner.repository.UserRepository;
import com.kacper.wedding_planner.service.impl.EventServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class EventServiceImplTest {

    private EventRepository eventRepository;
    private EventServiceImpl eventService;
    private User user;

    @BeforeEach
    void setUp() {
        eventRepository = mock(EventRepository.class);
        eventService = new EventServiceImpl(eventRepository);

        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
    }

    @Test
    void getEventsForUserShouldReturnEvents() {

        Event event1 = new Event();
        event1.setTitle("Wedding");
        event1.setDate(LocalDate.now());

        Event event2 = new Event();
        event2.setTitle("Reception");
        event2.setDate(LocalDate.now().plusDays(1));

        when(eventRepository.findByUser(user)).thenReturn(List.of(event1, event2));

        List<EventDTO> result = eventService.getEventsForUser(user);

        assertEquals(2, result.size());
        assertEquals("Wedding", result.get(0).getTitle());
        assertEquals("Reception", result.get(1).getTitle());

        verify(eventRepository.findByUser(user));
    }

    @Test
    void saveEventForUserShouldSetUserAndSave() {

        Event event = new Event();

        eventService.saveEventForUser(event, user);

        assertEquals(user, event.getUser());
        verify(eventRepository).save(event);
    }

}