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
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    
}

