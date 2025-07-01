package com.kacper.wedding_planner.mapper;

import com.kacper.wedding_planner.dto.EventDTO;
import com.kacper.wedding_planner.model.Event;

public class EventMapper {
    public static EventDTO toDTO(Event event) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDate(event.getDate());
        return dto;
    }
}
