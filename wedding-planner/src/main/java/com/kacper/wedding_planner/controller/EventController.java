package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.config.CustomUserDetails;
import com.kacper.wedding_planner.dto.EventDTO;
import com.kacper.wedding_planner.mapper.EventMapper;
import com.kacper.wedding_planner.model.Event;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.EventRepository;
import com.kacper.wedding_planner.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/events")
public class EventController {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventController(EventRepository eventRepository, UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String showCalendarPage() {
        return "events";
    }

    @GetMapping("/data")
    @ResponseBody
    public List<EventDTO> getEventsJson(@AuthenticationPrincipal CustomUserDetails principal) {
        List<Event> events = eventRepository.findByUserEmail(principal.getUsername());
        return events.stream()
                .map(EventMapper::toDTO)
                .toList();
    }

    @PostMapping
    @ResponseBody
    public EventDTO createEvent(@RequestBody Event event, @AuthenticationPrincipal CustomUserDetails principal) {
        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));

        event.setUser(user);
        Event saved = eventRepository.save(event);
        return EventMapper.toDTO(saved);
    }

    @PutMapping("/{id}")
    @ResponseBody
    public EventDTO updateEvent(@PathVariable Long id, @RequestBody Event updated, @AuthenticationPrincipal CustomUserDetails principal) {
        return eventRepository.findById(id)
                .filter(event -> event.getUser().getEmail().equals(principal.getUsername()))
                .map(event -> {
                    event.setTitle(updated.getTitle());
                    event.setDate(updated.getDate());
                    Event saved = eventRepository.save(event);
                    return EventMapper.toDTO(saved);
                }).orElseThrow(() -> new RuntimeException("Event not found or unauthorized"));
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public void deleteEvent(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails principal) {
        eventRepository.findById(id)
                .filter(event -> event.getUser().getEmail().equals(principal.getUsername()))
                .ifPresent(eventRepository::delete);
    }

}
