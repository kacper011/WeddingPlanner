package com.kacper.wedding_planner.service.impl;

import com.kacper.wedding_planner.dto.EventDTO;
import com.kacper.wedding_planner.exception.ResourceNotFoundException;
import com.kacper.wedding_planner.mapper.EventMapper;
import com.kacper.wedding_planner.model.Event;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.EventRepository;
import com.kacper.wedding_planner.service.EventService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public List<EventDTO> getEventsForUser(User user) {
        return eventRepository.findByUser(user)
                .stream()
                .map(EventMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Event> getEventByIdForUser(Long id, User user) {
        return eventRepository.findByIdAndUser(id, user);
    }

    @Transactional
    @Override
    public void saveEventForUser(Event event, User user) {
        event.setUser(user);
        eventRepository.save(event);
    }

    @Override
    public Event save(Event event) {
        return eventRepository.save(event);
    }

    @Transactional
    @Override
    public void deleteEventForUser(Long id, User user) {
        Event event = eventRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        eventRepository.delete(event);
    }
}
