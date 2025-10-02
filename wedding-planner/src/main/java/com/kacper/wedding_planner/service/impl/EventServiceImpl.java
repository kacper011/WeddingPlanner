package com.kacper.wedding_planner.service.impl;

import com.kacper.wedding_planner.model.Event;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.EventRepository;
import com.kacper.wedding_planner.repository.UserRepository;
import com.kacper.wedding_planner.service.EventService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Event> getEventsForUser(String email) {
        return eventRepository.findByUserEmail(email);
    }

    @Transactional
    @Override
    public void saveEventForUser(Event event, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
        event.setUser(user);
        eventRepository.save(event);
    }
}
