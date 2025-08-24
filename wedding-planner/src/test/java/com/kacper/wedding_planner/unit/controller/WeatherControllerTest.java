package com.kacper.wedding_planner.unit.controller;

import com.kacper.wedding_planner.config.CustomUserDetails;
import com.kacper.wedding_planner.controller.WeatherController;
import com.kacper.wedding_planner.dto.WeatherResponse;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WeatherController.class)
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private RestTemplate restTemplate;

    @BeforeEach
    void setupSecurityContext() {
        // Setup user and principal
        User user = new User();
        user.setEmail("test@example.com");

        CustomUserDetails principal = new CustomUserDetails(user);

        // Set authentication context
        var authentication = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void shouldReturnWeatherData() throws Exception {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");

        WeatherResponse response = new WeatherResponse();

        when(userService.findByEmail("test@example.com")).thenReturn(user);
        when(restTemplate.getForObject(anyString(), eq(WeatherResponse.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/guests/countdown/weather")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
