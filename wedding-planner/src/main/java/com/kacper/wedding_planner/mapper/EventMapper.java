package com.kacper.wedding_planner.mapper;

import com.kacper.wedding_planner.dto.EventDTO;
import com.kacper.wedding_planner.dto.EventRequest;
import com.kacper.wedding_planner.model.Event;
import com.kacper.wedding_planner.model.User;

public class EventMapper {

    public static EventDTO toDTO(Event event) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDate(event.getDate());
        return dto;
    }

    public static Event toEntity(EventRequest request, User user) {
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDate(request.getDate());
        event.setUser(user);
        return event;
    }
}
