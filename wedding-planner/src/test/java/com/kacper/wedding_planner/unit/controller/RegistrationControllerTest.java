package com.kacper.wedding_planner.unit.controller;

import com.kacper.wedding_planner.config.SecurityConfig;
import com.kacper.wedding_planner.controller.RegistrationController;
import com.kacper.wedding_planner.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistrationController.class)
@Import(SecurityConfig.class)
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void shouldShowRegistrationForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registrationDto"));
    }

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        doNothing().when(userService).registerUser(anyString(), anyString(), anyString());

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("email", "test@test.com")
                        .param("password", "12345678")
                        .param("confirmPassword", "12345678")
                        .param("firstName", "John"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?success"));
    }

    @Test
    void shouldReturnErrorOnRegistrationFailure() throws Exception {
        doThrow(new IllegalArgumentException("Email already exists"))
                .when(userService)
                .registerUser("test@example.com", "secret123", "John");

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("email", "test@example.com")
                        .param("password", "secret123")
                        .param("confirmPassword", "secret123")
                        .param("firstName", "John"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Email already exists"));
    }
}