package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.config.CustomUserDetails;
import com.kacper.wedding_planner.dto.EventDTO;
import com.kacper.wedding_planner.dto.EventRequest;
import com.kacper.wedding_planner.mapper.EventMapper;
import com.kacper.wedding_planner.model.Event;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.EventRepository;
import com.kacper.wedding_planner.repository.UserRepository;
import com.kacper.wedding_planner.service.EventService;
import com.kacper.wedding_planner.service.UserService;
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

    private final EventService eventService;
    private final UserService userService;

    public EventController(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    @GetMapping
    public String showCalendarPage() {
        return "events";
    }

    @GetMapping("/data")
    @ResponseBody
    public ResponseEntity<List<EventDTO>> getEventsJson(
            @AuthenticationPrincipal CustomUserDetails principal) {

        User user = userService.findByEmail(principal.getUsername());

        List<EventDTO> events = eventService.getEventsForUser(user);

        return events.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(events);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<EventDTO> createEvent(
            @RequestBody EventRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {

        User user = userService.findByEmail(principal.getUsername());

        Event event = EventMapper.toEntity(request, user);
        eventService.saveEventForUser(event, user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(EventMapper.toDTO(event));
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<EventDTO> updateEvent(
            @PathVariable Long id,
            @RequestBody EventRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {

        User user = userService.findByEmail(principal.getUsername());

        Event event = eventService.getEventByIdForUser(id, user)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Event not found"));

        event.setTitle(request.getTitle());
        event.setDate(request.getDate());

        Event updated = eventService.save(event);

        return ResponseEntity.ok(EventMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal) {

        User user = userService.findByEmail(principal.getUsername());

        eventService.deleteEventForUser(id, user);

        return ResponseEntity.noContent().build();
    }
}
