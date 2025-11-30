package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.config.CustomUserDetails;
import com.kacper.wedding_planner.dto.EventDTO;
import com.kacper.wedding_planner.dto.EventRequest;
import com.kacper.wedding_planner.mapper.EventMapper;
import com.kacper.wedding_planner.model.Event;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.EventRepository;
import com.kacper.wedding_planner.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    public ResponseEntity<List<EventDTO>> getEventsJson(@AuthenticationPrincipal CustomUserDetails principal) {
        List<EventDTO> events = eventRepository.findByUserEmail(principal.getUsername()).stream()
                .map(EventMapper::toDTO)
                .toList();

        return events.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(events);
    }

    @PostMapping
    public ResponseEntity<EventDTO> createEvent(@RequestBody EventRequest request,
                                                @AuthenticationPrincipal CustomUserDetails principal) {

        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Event event = EventMapper.toEntity(request, user);
        Event saved = eventRepository.save(event);

        return ResponseEntity.status(HttpStatus.CREATED).body(EventMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> updateEvent(
            @PathVariable Long id,
            @RequestBody EventRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        if (!event.getUser().getEmail().equals(principal.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized");
        }
        
        event.setTitle(request.getTitle());
        event.setDate(request.getDate());

        Event saved = eventRepository.save(event);
        return ResponseEntity.ok(EventMapper.toDTO(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        if (!event.getUser().getEmail().equals(principal.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized");
        }

        eventRepository.delete(event);
        return ResponseEntity.noContent().build();
    }
}
