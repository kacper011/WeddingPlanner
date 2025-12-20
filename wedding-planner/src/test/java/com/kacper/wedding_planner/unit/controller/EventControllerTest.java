package com.kacper.wedding_planner.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kacper.wedding_planner.config.CustomUserDetails;
import com.kacper.wedding_planner.controller.EventController;
import com.kacper.wedding_planner.dto.EventDTO;
import com.kacper.wedding_planner.dto.EventRequest;
import com.kacper.wedding_planner.model.Event;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.EventRepository;
import com.kacper.wedding_planner.repository.UserRepository;
import com.kacper.wedding_planner.service.EventService;
import com.kacper.wedding_planner.service.UserService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldReturnEventsJson() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        EventDTO dto = new EventDTO();
        dto.setTitle("Test event");

        when(userService.findByEmail("test@example.com"))
                .thenReturn(user);

        when(eventService.getEventsForUser(user))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/events/data"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("Test event"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldReturnNoContentWhenNoEvents() throws Exception {

        User testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        when(userService.findByEmail("test@example.com"))
                .thenReturn(testUser);

        when(eventService.getEventsForUser(testUser))
                .thenReturn(List.of());

        mockMvc.perform(get("/events/data"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldCreateEvent() throws Exception {

        EventRequest request = new EventRequest();
        request.setTitle("New Event");
        request.setDate(LocalDate.of(2025, 6, 1));

        when(userService.findByEmail("test@example.com"))
                .thenReturn(testUser);

        doNothing().when(eventService)
                        .saveEventForUser(any(Event.class), eq(testUser));

        mockMvc.perform(post("/events")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldUpdateEvent() throws Exception {

        Event existing = new Event();
        existing.setId(1L);
        existing.setTitle("Old Title");
        existing.setDate(LocalDate.of(2025, 1, 1));
        existing.setUser(testUser);

       EventRequest request = new EventRequest();
       request.setTitle("New Title");
       request.setDate(LocalDate.of(2025, 12, 25));

       when(userService.findByEmail("test@example.com"))
               .thenReturn(testUser);

       when(eventService.getEventByIdForUser(1L, testUser))
               .thenReturn(Optional.of(existing));

       mockMvc.perform(put("/events/1")
               .with(csrf())
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.title").value("New Title"));

    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldDeleteEvent() throws Exception {

        when(userService.findByEmail("test@example.com"))
                .thenReturn(testUser);

        when(eventService.deleteEventForUser(1L, testUser))
                .thenReturn(true);

        mockMvc.perform(delete("/events/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(eventService).deleteEventForUser(1L, testUser);
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldReturnEventsView() throws Exception {
        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(view().name("events"));
    }

}