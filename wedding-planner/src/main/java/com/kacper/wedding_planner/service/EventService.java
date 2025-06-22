package com.kacper.wedding_planner.service;

import com.kacper.wedding_planner.model.Event;

import java.util.List;

public interface EventService {

    public List<Event> getEventsForUser(String email);

    public void saveEventForUser(Event event, String email);
}
