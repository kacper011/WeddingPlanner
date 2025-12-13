package com.kacper.wedding_planner.service;

import com.kacper.wedding_planner.dto.EventDTO;
import com.kacper.wedding_planner.model.Event;
import com.kacper.wedding_planner.model.User;

import java.util.List;
import java.util.Optional;

public interface EventService {

    List<EventDTO> getEventsForUser(User user);

    void saveEventForUser(Event event, User user);

    Optional<Event> getEventByIdForUser(Long id, User user);

    Event save(Event event);

    void deleteEventForUser(Long id, User user);
}
