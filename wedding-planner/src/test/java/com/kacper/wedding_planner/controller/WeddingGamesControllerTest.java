package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.config.CustomUserDetails;
import com.kacper.wedding_planner.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WeddingGamesController.class)
@ExtendWith(SpringExtension.class)
public class WeddingGamesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WeddingGamesController weddingGamesController;

    @BeforeEach
    void setupSecurityContext() {

        User user = new User();
        user.setEmail("test@example.com");

        CustomUserDetails principal = new CustomUserDetails(user);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    @Test
    public void shouldWeddingGamesPageReturnsViewWithQuestions() throws Exception {

        weddingGamesController.init();
        // Act & Assert
        mockMvc.perform(get("/wedding_games")
                        .accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("wedding_games"))
                .andExpect(model().attributeExists("questions"));
    }

    @Test
    public void shouldInitMethodLoadsQuestions() throws IOException {
        // Arrange
        WeddingGamesController controller = new WeddingGamesController();
        controller.init();

        // Assert
        assertThat(controller).hasFieldOrProperty("questions");
        assertThat(controller.weddingGamesPage(new org.springframework.ui.ExtendedModelMap()))
                .isEqualTo("wedding_games");
    }


}
