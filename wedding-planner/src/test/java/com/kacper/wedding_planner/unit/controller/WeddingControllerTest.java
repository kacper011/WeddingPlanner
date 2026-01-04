package com.kacper.wedding_planner.unit.controller;

import com.kacper.wedding_planner.config.CustomUserDetails;
import com.kacper.wedding_planner.controller.WeddingController;
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
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");

        CustomUserDetails principal = new CustomUserDetails(mockUser);
        var authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        mockGuests = new ArrayList<>(List.of(
                createGuest("Kowalski", GuestCategory.GROOM_FAMILY, "TAK", "TAK"),
                createGuest("Nowak", GuestCategory.FRIENDS, "NIE", "NIE")
        ));

        when(userService.findByEmail("test@example.com")).thenReturn(mockUser);
        when(guestRepository.findByUser(mockUser)).thenReturn(mockGuests);
    }

    private Guest createGuest(String lastName, GuestCategory category, String attendanceConfirmation, String afterParty) {
        Guest guest = new Guest();
        guest.setLastName(lastName);
        guest.setCategory(category);
        guest.setAttendanceConfirmation(attendanceConfirmation);
        guest.setAfterParty(afterParty);
        return guest;
    }

    @Test
    void shouldListGuestsWithoutCategoryFilter() throws Exception {
        User mockUser = new User();
        mockUser.setEmail("test@example.com");

        Guest guest1 = new Guest();
        guest1.setFirstName("Jan");
        guest1.setLastName("Kowalski");
        guest1.setAttendanceConfirmation("YES");
        guest1.setAfterParty("YES");
        guest1.setCategory(GuestCategory.GROOM_FAMILY);

        Guest guest2 = new Guest();
        guest2.setFirstName("Anna");
        guest2.setLastName("Nowak");
        guest2.setAttendanceConfirmation("NO");
        guest2.setAfterParty("NO");
        guest2.setCategory(GuestCategory.FRIENDS);

        when(userService.findByEmail(anyString())).thenReturn(mockUser);
        when(guestRepository.findByUser(any(User.class)))
                .thenReturn(new ArrayList<>(List.of(guest1, guest2)));

        mockMvc.perform(get("/guests")
                        .principal(() -> "test@example.com")
                        .param("category", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("guests"))
                .andExpect(model().attributeExists(
                        "guests", "totalGuests", "confirmedGuests", "notConfirmedGuests", "receptionGuests",
                        "categories", "selectedCategory"
                ));
    }

    @Test
    void shouldShowCreateGuestForm() throws Exception {
        mockMvc.perform(get("/guests/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("add_guest"))
                .andExpect(model().attributeExists("guest"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attribute("guest", org.hamcrest.Matchers.hasProperty("category", org.hamcrest.Matchers.nullValue())))
                .andExpect(model().attribute("categories", GuestCategory.values()));
    }

    @Test
    void shouldAddGuestSuccessfullyWhenFormIsValid() throws Exception {
        mockMvc.perform(post("/guests")
                        .with(csrf())
                        .param("firstName", "Jan")
                        .param("lastName", "Kowalski")
                        .param("category", "GROOM_FAMILY")
                        .param("contact", "123 123 123"))
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
                .andExpect(model().attributeHasFieldErrors("guest", "firstName"))
                .andExpect(model().attributeExists("categories"));

        verify(guestService, never()).saveGuest(any());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void shouldReturnGuestDetailsView() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        guest = createGuest("Kowalski", GuestCategory.GROOM_FAMILY, "TAK", "TAK");
        guest.setId(1L);
        guest.setUser(user);

        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest));
        when(userService.findByEmail("test@example.com")).thenReturn(user);

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

        mockMvc.perform(post("/guests")
                .with(csrf())
                .param("firstName", "Jan")
                .param("lastName", "Kowalski")
                .param("category", "GROOM_FAMILY")
                .param("attendanceConfirmation", "TAK")
                .param("afterParty", "TAK"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/guests"));

        verify(guestService).saveGuest(any(Guest.class));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void shouldReturnToFormWhenGuestDataIsInvalid() throws Exception {

        mockMvc.perform(post("/guests")
                        .with(csrf())
                        .param("lastName", "Kowalski")
                        .param("category", "RODZINA_PANA_MLODEGO"))
                .andExpect(status().isOk())
                .andExpect(view().name("add_guest"))
                .andExpect(model().attributeHasFieldErrors("guest", "firstName"))
                .andExpect(model().attributeExists("categories"));

        verify(guestRepository, never()).save(any());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void shouldEditGuestSuccessfully() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        Guest existingGuest = createGuest("Kowalski", GuestCategory.GROOM_FAMILY, "TAK", "TAK");
        existingGuest.setId(1L);
        existingGuest.setUser(user);

        when(guestRepository.findById(1L)).thenReturn(Optional.of(existingGuest));
        when(userService.findByEmail("test@example.com")).thenReturn(mockUser);

        mockMvc.perform(post("/guests/edit/1")
                        .with(csrf())
                        .param("firstName", "Jan")
                        .param("lastName", "Nowak")
                        .param("attendanceConfirmation", "TAK")
                        .param("afterParty", "NIE")
                        .param("category", "GROOM_FAMILY"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/guests"));

        verify(guestRepository).save(argThat(savedGuest ->
                savedGuest.getId().equals(1L) &&
                        savedGuest.getLastName().equals("Nowak") &&
                        savedGuest.getFirstName().equals("Jan") &&
                        savedGuest.getCategory() == GuestCategory.GROOM_FAMILY &&
                        savedGuest.getUser().equals(mockUser)
        ));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void shouldReturnErrorPageWhenGuestNotFound() throws Exception {
        // given
        Long guestId = 99L;
        when(guestRepository.findById(guestId)).thenReturn(Optional.empty());

        // when & then
        mockMvc.perform(post("/guests/edit/{id}", guestId)
                        .with(csrf())
                        .param("nazwisko", "Nowak")
                        .param("imie", "Jan")
                        .param("kategoria", GuestCategory.GROOM_FAMILY.name()))
                .andExpect(status().isNotFound());

        verify(guestRepository, never()).save(any());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void shouldReturn403WhenGuestBelongsToAnotherUser() throws Exception {

        User otherUser = new User();
        otherUser.setId(2L);

        Guest guest = new Guest();
        guest.setId(1L);
        guest.setUser(otherUser);

        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest));

        mockMvc.perform(post("/guests/edit/{id}", 1L)
                .with(csrf())
                .param("nazwisko", "Nowak")
                .param("imie", "Jan")
                .param("kategora", GuestCategory.GROOM_FAMILY.name()))
                .andExpect(status().isForbidden());

        verify(guestRepository, never()).save(any());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void shouldDeleteGuestAndRedirect() throws Exception {

        Long guestId = 1L;

        User testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        Guest guest = new Guest();
        guest.setId(guestId);
        guest.setUser(testUser);

        when(userService.findByEmail("test@example.com"))
                .thenReturn(testUser);

        when(guestRepository.findById(guestId)).thenReturn(Optional.of(guest));

        mockMvc.perform(post("/guests/delete/{id}", guestId)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/guests"))
                .andExpect(flash().attribute("message", "Gość został pomyślnie usunięty."));

        verify(guestService, times(1)).deleteGuest(guestId, testUser);
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void updatePresenceShouldUpdateGuestAndRedirect() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        Guest guest = new Guest();
        guest.setId(1L);
        guest.setUser(user);
        guest.setAttendanceConfirmation("TAK");

        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest));
        when(guestRepository.save(any(Guest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/guests/updatePresence/1")
                .param("presence", "NIE")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/guests"));

        assertEquals("NIE", guest.getAttendanceConfirmation());
        assertNull(guest.getTransport());
        assertNull(guest.getAccommodation());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void updateTransportShouldUpdateGuestAndRedirect() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        Guest guest = new Guest();
        guest.setId(1L);
        guest.setUser(user);
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
        guest.setUser(mockUser);

        when(guestRepository.findById(2L)).thenReturn(Optional.of(guest));
        when(guestRepository.save(any(Guest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/guests/updateTransport/2")
                .param("transport", "NIE")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/guests"));

        assertEquals("NIE", guest.getTransport());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void updateTransportShouldReturn4XXWhenGuestNotFound() throws Exception {

        when(guestRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/guests/updateTransport/99")
                        .param("transport", "TAK")
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser("testuser@example.com")
    void shouldUpdateLodgingAndRedirect() throws Exception {

        User user = new User();
        user.setId(1L);
        user.setEmail("testuser@example.com");

        Long guestId = 1L;
        Guest guest = new Guest();
        guest.setId(guestId);
        guest.setUser(user);

        when(guestRepository.findById(guestId)).thenReturn(Optional.of(guest));

        mockMvc.perform(post("/guests/updateLodging/{id}", guestId)
                .with(csrf())
                .param("lodging", "TAK"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/guests"));

        assertEquals("TAK", guest.getAccommodation());
        verify(guestRepository).save(guest);
    }
}

