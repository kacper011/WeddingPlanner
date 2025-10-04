package com.kacper.wedding_planner.service.impl;

import com.kacper.wedding_planner.dto.EventDTO;
import com.kacper.wedding_planner.exception.UserNotFoundException;
import com.kacper.wedding_planner.mapper.EventMapper;
import com.kacper.wedding_planner.model.Event;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.EventRepository;
import com.kacper.wedding_planner.repository.UserRepository;
import com.kacper.wedding_planner.service.EventService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<EventDTO> getEventsForUser(String email) {
        return eventRepository.findByUserEmail(email)
                .stream()
                .map(EventMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void saveEventForUser(Event event, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
        event.setUser(user);
        eventRepository.save(event);
    }
}
