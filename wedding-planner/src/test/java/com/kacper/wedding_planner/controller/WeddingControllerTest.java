package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.config.CustomUserDetails;
import com.kacper.wedding_planner.model.Guest;
import com.kacper.wedding_planner.model.GuestCategory;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.GuestRepository;
import com.kacper.wedding_planner.repository.WeddingInfoRepository;
import com.kacper.wedding_planner.service.GuestService;
import com.kacper.wedding_planner.service.UserService;
import com.kacper.wedding_planner.service.WeddingInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WeddingController.class)
class WeddingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GuestService guestService;

    @MockBean
    private GuestRepository guestRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private WeddingInfoRepository weddingInfoRepository;

    @MockBean
    private WeddingInfoService weddingInfoService;

    private Guest guest;

    private User mockUser;
    private List<Guest> mockGuests;

    @BeforeEach
    void setupSecurityContext() {
        mockUser = new User();
        mockUser.setEmail("test@example.com");

        CustomUserDetails principal = new CustomUserDetails(mockUser);
        var authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockGuests = new ArrayList<>(List.of(
                createGuest("Kowalski", GuestCategory.RODZINA_PANA_MLODEGO, "TAK", "TAK"),
                createGuest("Nowak", GuestCategory.ZNAJOMI, "NIE", "NIE")
        ));

        when(userService.findByEmail("test@example.com")).thenReturn(mockUser);
        when(guestRepository.findByUser(mockUser)).thenReturn(mockGuests);
    }

    private Guest createGuest(String nazwisko, GuestCategory kategoria, String potwierdzenie, String poprawiny) {
        Guest guest = new Guest();
        guest.setNazwisko(nazwisko);
        guest.setKategoria(kategoria);
        guest.setPotwierdzenieObecnosci(potwierdzenie);
        guest.setPoprawiny(poprawiny);
        return guest;
    }

    @Test
    void shouldListGuestsWithoutCategoryFilter() throws Exception {
        mockMvc.perform(get("/guests"))
                .andExpect(status().isOk())
                .andExpect(view().name("guests"))
                .andExpect(model().attributeExists("guests"))
                .andExpect(model().attribute("totalGuests", 2L))
                .andExpect(model().attribute("confirmedGuests", 1L))
                .andExpect(model().attribute("notConfirmedGuests", 1L))
                .andExpect(model().attribute("receptionGuests", 1L));
    }

    @Test
    void shouldShowCreateGuestForm() throws Exception {
        mockMvc.perform(get("/guests/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("add_guest"))
                .andExpect(model().attributeExists("guest"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attribute("guest", org.hamcrest.Matchers.hasProperty("kategoria", org.hamcrest.Matchers.nullValue())))
                .andExpect(model().attribute("categories", GuestCategory.values()));
    }

    @Test
    void shouldAddGuestSuccessfullyWhenFormIsValid() throws Exception {
        mockMvc.perform(post("/guests")
                .with(csrf())
                .param("name", "Jan Kowalski")
                .param("category", "RODZINA_PANA_MLODEGO")
                .param("present", "TAK")
                .param("confirmed", "NIE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/guests"));

        verify(guestService).saveGuest(any(Guest.class));
    }

    @Test
    void shouldReturnFormWithErrorsWhenFormIsInvalid() throws Exception {
        mockMvc.perform(post("/guests")
                .with(csrf())
                .param("category", "ZNAJOMI")
                .param("present", "NIE")
                .param("confirmed", "TAK"))
                .andExpect(status().isOk())
                .andExpect(view().name("add_guest"))
                .andExpect(model().attributeHasFieldErrors("guest", "imie"))
                .andExpect(model().attributeExists("categories"));

        verify(guestService, never()).saveGuest(any());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void shouldReturnGuestDetailsView() throws Exception {

        guest = createGuest("Kowalski", GuestCategory.RODZINA_PANA_MLODEGO, "TAK", "TAK");
        guest.setId(1L);
        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest));

        mockMvc.perform(get("/guests/details/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("guest_details"))
                .andExpect(model().attributeExists("guest"))
                .andExpect(model().attribute("guest", guest));
    }
}

