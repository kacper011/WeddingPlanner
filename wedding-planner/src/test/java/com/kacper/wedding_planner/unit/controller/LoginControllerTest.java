package com.kacper.wedding_planner.unit.controller;

import com.kacper.wedding_planner.controller.LoginController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
@AutoConfigureMockMvc(addFilters = false)
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnLoginViewWithoutParams() throws Exception {
        mockMvc.perform(get("/login")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeDoesNotExist("errorMessage"))
                .andExpect(model().attributeDoesNotExist("msg"));
    }

    @Test
    void shouldShowErrorMessageWhenErrorParamPresent() throws Exception {
        mockMvc.perform(get("/login").param("error", "true")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Nieprawidłowy login lub hasło"));
    }

    @Test
    void shouldShowLogoutMessageWhenLogoutParamPresent() throws Exception {
        mockMvc.perform(get("/login").param("logout", "true")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attributeExists("msg"))
                .andExpect(model().attribute("msg", "Zostałeś wylogowany pomyślnie"));
    }
}