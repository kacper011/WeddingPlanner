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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void shouldReturn404WhenGuestNotFound() throws Exception {
        when(guestRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/guests/details/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void shouldCreateGuestSuccessfully() throws Exception {
        when(userService.findByEmail("test@example.com")).thenReturn(mockUser);

        mockMvc.perform(post("/guests/create")
                .with(csrf())
                .param("imie", "Jan")
                .param("nazwisko", "Kowalski")
                .param("kategoria", "RODZINA_PANA_MLODEGO")
                .param("potwierdzenieObecnosci", "TAK")
                .param("poprawiny", "TAK"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/guests"));

        verify(guestRepository).save(any(Guest.class));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void shouldReturnToFormWhenGuestDataIsInvalid() throws Exception {
        mockMvc.perform(post("/guests/create")
                        .with(csrf())
                        .param("nazwisko", "Kowalski")
                        .param("kategoria", "RODZINA_PANA_MLODEGO"))
                .andExpect(status().isOk())
                .andExpect(view().name("add_guest"))
                .andExpect(model().attributeHasFieldErrors("guest", "imie"))
                .andExpect(model().attributeExists("categories"));

        verify(guestRepository, never()).save(any());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void shouldEditGuestSuccessfully() throws Exception {

        Guest existingGuest = createGuest("Kowalski", GuestCategory.RODZINA_PANA_MLODEGO, "TAK", "TAK");
        existingGuest.setId(1L);

        when(guestRepository.findById(1L)).thenReturn(Optional.of(existingGuest));
        when(userService.findByEmail("test@example.com")).thenReturn(mockUser);

        mockMvc.perform(post("/guests/edit/1")
                        .with(csrf())
                        .param("imie", "Jan")
                        .param("nazwisko", "Nowak")
                        .param("potwierdzenieObecnosci", "TAK")
                        .param("poprawiny", "NIE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/guests/confirmed"));

        verify(guestRepository).save(argThat(savedGuest ->
                savedGuest.getId().equals(1L) &&
                        savedGuest.getNazwisko().equals("Nowak") &&
                        savedGuest.getImie().equals("Jan") &&
                        savedGuest.getKategoria() == GuestCategory.RODZINA_PANA_MLODEGO &&
                        savedGuest.getUser().equals(mockUser)
        ));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void shouldReturnErrorPageWhenGuestNotFound() throws Exception {
        // given
        when(guestRepository.findById(99L)).thenReturn(Optional.empty());

        // when & then
        mockMvc.perform(post("/guests/edit/99")
                        .with(csrf())
                        .param("nazwisko", "Nowak")
                        .param("imie", "Jan")
                        .param("kategoria", GuestCategory.RODZINA_PANA_MLODEGO.name()))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));

        verify(guestRepository, never()).save(any());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void shouldDeleteGuestAndRedirect() throws Exception {

        Long guestId = 1L;

        mockMvc.perform(post("/guests/delete/{id}", guestId)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/guests"))
                .andExpect(flash().attribute("message", "Gość został pomyślnie usunięty."));

        verify(guestService, times(1)).deleteGuest(guestId);
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void updatePresenceShouldUpdateGuestAndRedirect() throws Exception {

        Guest guest = new Guest();
        guest.setId(1L);
        guest.setPotwierdzenieObecnosci("TAK");

        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest));
        when(guestRepository.save(any(Guest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/guests/updatePresence/1")
                .param("presence", "NIE")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/guests"));

        assertEquals("NIE", guest.getPotwierdzenieObecnosci());
        assertNull(guest.getTransport());
        assertNull(guest.getNocleg());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void updateTransportShouldUpdateGuestAndRedirect() throws Exception {

        Guest guest = new Guest();
        guest.setId(1L);
        guest.setTransport(null);

        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest));
        when(guestRepository.save(any(Guest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/guests/updateTransport/1")
                .param("transport", "TAK")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/guests"));

        assertEquals("TAK", guest.getTransport());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void updateTransportShouldSetTransportToNoAndRedirect() throws Exception {

        Guest guest = new Guest();
        guest.setId(2L);
        guest.setTransport("TAK");

        when(guestRepository.findById(2L)).thenReturn(Optional.of(guest));
        when(guestRepository.save(any(Guest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/guests/updateTransport/2")
                .param("transport", "NIE")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/guests"));

        assertEquals("NIE", guest.getTransport());
    }
}

