package com.kacper.wedding_planner.service;

import com.kacper.wedding_planner.dto.EventDTO;
import com.kacper.wedding_planner.model.Event;
import com.kacper.wedding_planner.model.User;

import java.util.List;

public interface EventService {

    List<EventDTO> getEventsForUser(User user);

    void saveEventForUser(Event event, User user);
}
