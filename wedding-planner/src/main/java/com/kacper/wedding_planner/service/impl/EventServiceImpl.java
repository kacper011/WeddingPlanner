package com.kacper.wedding_planner.service.impl;

import com.kacper.wedding_planner.model.Event;
import com.kacper.wedding_planner.service.EventService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventServiceImpl implements EventService {
    @Override
    public List<Event> getEventsForUser(String email) {
        return null;
    }

    @Override
    public void saveEventForUser(Event event, String email) {

    }
}
