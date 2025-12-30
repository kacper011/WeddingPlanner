package com.kacper.wedding_planner.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kacper.wedding_planner.config.CustomUserDetails;
import com.kacper.wedding_planner.controller.WeddingTaskController;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.model.WeddingTask;
import com.kacper.wedding_planner.service.UserService;
import com.kacper.wedding_planner.service.WeddingTaskService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WeddingTaskController.class)
class WeddingTaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeddingTaskService weddingTaskService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private CustomUserDetails principal;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
    }

    @Test
    void shouldShowChecklist() throws Exception {

        WeddingTask task = new WeddingTask();
        task.setId(1L);
        task.setName("Test Task");
        task.setUser(testUser);

        when(userService.findByEmail("test@example.com")).thenReturn(testUser);
        when(weddingTaskService.getForUser(testUser)).thenReturn(List.of(task));

        mockMvc.perform(get("/checklist")
                        .with(user(new CustomUserDetails(testUser))))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("tasks"))
                .andExpect(model().attributeExists("newTask"))
                .andExpect(view().name("checklist"));
    }

    @Test
    void shouldAddTask() throws Exception {

        when(userService.findByEmail("test@example.com")).thenReturn(testUser);

        mockMvc.perform(post("/checklist")
                .with(user(new CustomUserDetails(testUser)))
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "New Task"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/checklist"));

        verify(weddingTaskService).save(any(WeddingTask.class));
    }

    @Test
    void shouldDeleteTask() throws Exception {

        WeddingTask task = new WeddingTask();
        task.setId(5L);
        task.setUser(testUser);

        when(userService.findByEmail("test@example.com")).thenReturn(testUser);
        when(weddingTaskService.getById(5L)).thenReturn(task);

        mockMvc.perform(get("/checklist/delete/5")
                        .with(user(new CustomUserDetails(testUser))))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/checklist"));

        verify(weddingTaskService).delete(5L);
    }

    @Test
    void shouldToggleTask() throws Exception {

        WeddingTask task = new WeddingTask();
        task.setId(3L);
        task.setUser(testUser);

        when(userService.findByEmail("test@example.com")).thenReturn(testUser);
        when(weddingTaskService.getById(3L)).thenReturn(task);

        mockMvc.perform(post("/checklist/toggle/3")
                        .with(user(new CustomUserDetails(testUser)))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/checklist"));

        verify(weddingTaskService).toggleForUser(3L, testUser);

    }
}
