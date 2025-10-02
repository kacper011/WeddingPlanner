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
    private UserRepository userRepository;
    private EventServiceImpl eventService;

    @BeforeEach
    void setUp() {
        eventRepository = mock(EventRepository.class);
        userRepository = mock(UserRepository.class);
        eventService = new EventServiceImpl(eventRepository, userRepository);
    }

    @Test
    void getEventsForUserShouldReturnEvents() {
        String email = "test@test.com";

        Event event1 = new Event();
        event1.setTitle("Wedding");
        event1.setDate(LocalDate.now());

        Event event2 = new Event();
        event2.setTitle("Reception");
        event2.setDate(LocalDate.now().plusDays(1));

        List<Event> expectedEvents = List.of(event1, event2);

        when(eventRepository.findByUserEmail(email)).thenReturn(expectedEvents);

        List<EventDTO> result = eventService.getEventsForUser(email);

        assertEquals(expectedEvents.size(), result.size());
        assertEquals(expectedEvents.get(0).getTitle(), result.get(0).getTitle());
        assertEquals(expectedEvents.get(0).getDate(), result.get(0).getDate());
        assertEquals(expectedEvents.get(1).getTitle(), result.get(1).getTitle());
        assertEquals(expectedEvents.get(1).getDate(), result.get(1).getDate());

        verify(eventRepository, times(1)).findByUserEmail(email);
    }

    @Test
    void saveEventForUserShouldSetUserAndSave() {
        // given
        String email = "test@example.com";
        User mockUser = new User();
        mockUser.setEmail(email);

        Event event = new Event();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        // when
        eventService.saveEventForUser(event, email);

        // then
        assertEquals(mockUser, event.getUser());
        verify(eventRepository, times(1)).save(event);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void saveEventForUserShouldThrowWhenUserNotFound() {
        // given
        String email = "missing@example.com";
        Event event = new Event();
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // then
        assertThrows(UserNotFoundException.class, () -> {
            eventService.saveEventForUser(event, email);
        });

        verify(eventRepository, never()).save(any());
    }

}