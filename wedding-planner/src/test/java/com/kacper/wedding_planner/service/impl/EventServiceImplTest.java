package com.kacper.wedding_planner.service.impl;

import com.kacper.wedding_planner.model.Event;
import com.kacper.wedding_planner.repository.EventRepository;
import com.kacper.wedding_planner.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
        // given
        String email = "test@example.com";
        List<Event> expectedEvents = Arrays.asList(
                new Event(), new Event()
        );
        when(eventRepository.findByUserEmail(email)).thenReturn(expectedEvents);

        // when
        List<Event> result = eventService.getEventsForUser(email);

        // then
        assertEquals(expectedEvents, result);
        verify(eventRepository, times(1)).findByUserEmail(email);
    }



}