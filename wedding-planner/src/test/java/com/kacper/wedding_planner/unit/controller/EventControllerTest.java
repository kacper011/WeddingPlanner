package com.kacper.wedding_planner.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kacper.wedding_planner.config.CustomUserDetails;
import com.kacper.wedding_planner.controller.EventController;
import com.kacper.wedding_planner.model.Event;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.EventRepository;
import com.kacper.wedding_planner.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventRepository eventRepository;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private CustomUserDetails principal;

    private User testUser;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        principal = new CustomUserDetails(testUser);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldReturnEventsJson() throws Exception {
        Event event = new Event();
        event.setId(1L);
        event.setTitle("Test event");
        event.setDate(LocalDate.now());
        event.setUser(testUser);

        when(eventRepository.findByUserEmail("test@example.com"))
                .thenReturn(List.of(event));

        mockMvc.perform(get("/events/data"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("Test event"));
    }
    

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldCreateEvent() throws Exception {
        Event event = new Event();
        event.setTitle("New Event");
        event.setDate(LocalDate.now());

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        Event savedEvent = new Event();
        savedEvent.setId(1L);
        savedEvent.setTitle(event.getTitle());
        savedEvent.setDate(event.getDate());
        savedEvent.setUser(testUser);

        when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);

        mockMvc.perform(post("/events")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("New Event"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldUpdateEvent() throws Exception {
        Event existing = new Event();
        existing.setId(1L);
        existing.setTitle("Old Title");
        existing.setDate(LocalDate.of(2025, 1, 1));
        existing.setUser(testUser);

        Event updated = new Event();
        updated.setTitle("Updated Title");
        updated.setDate(LocalDate.of(2025, 12, 25));

        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(eventRepository.save(any(Event.class))).thenReturn(existing);

        mockMvc.perform(put("/events/1")
                        .with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
                        ))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldDeleteEvent() throws Exception {
        Event existing = new Event();
        existing.setId(1L);
        existing.setUser(testUser);

        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(existing));

        mockMvc.perform(delete("/events/1")
                        .with(csrf())
                        .with(authentication(
                                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
                        )))
                .andExpect(status().isOk());

        Mockito.verify(eventRepository).delete(existing);
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldReturnEventsView() throws Exception {
        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("events"));
    }

}