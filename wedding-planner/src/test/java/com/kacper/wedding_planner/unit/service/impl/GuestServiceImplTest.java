package com.kacper.wedding_planner.unit.service.impl;

import com.kacper.wedding_planner.exception.GuestNotFoundException;
import com.kacper.wedding_planner.model.Guest;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.GuestRepository;
import com.kacper.wedding_planner.service.impl.GuestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class GuestServiceImplTest {

    private GuestRepository guestRepository;
    private GuestServiceImpl guestService;

    @BeforeEach
    void setUp() {
        guestRepository = mock(GuestRepository.class);
        guestService = new GuestServiceImpl(guestRepository);
    }

    @Test
    void shouldReturnAllGuestsForUser() {
        User testUser = new User();
        List<Guest> guests = Arrays.asList(new Guest(), new Guest());

        when(guestRepository.findByUser(testUser)).thenReturn(guests);

        List<Guest> result = guestService.getAllGuestsByUser(testUser);

        assertEquals(2, result.size());
        verify(guestRepository).findByUser(testUser);
    }

    @Test
    void shouldDeleteGuestById() {

        Long guestId = 1L;

        User user = new User();
        user.setEmail("test@example.com");

        Guest guest = new Guest();
        guest.setId(guestId);
        guest.setUser(user);

        when(guestRepository.findByIdAndUser(guestId, user))
                .thenReturn(Optional.of(guest));

        guestService.deleteGuest(guestId, user);
        verify(guestRepository).delete(guest);
    }

    @Test
    void shouldUpdatePresence() {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        Guest guest = new Guest();
        guest.setId(1L);
        guest.setUser(user);
        guest.setAttendanceConfirmation("NIE");

        when(guestRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(guest));

        guestService.updatePresence(1L, "TAK", user);

        assertEquals("TAK", guest.getAttendanceConfirmation());
        verify(guestRepository).findByIdAndUser(1L, user);
        verify(guestRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingPresenceAndGuestNotFound() {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        when(guestRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.empty());

        GuestNotFoundException exception = assertThrows(
                GuestNotFoundException.class,
                () -> guestService.updatePresence(1L, "TAK", user)
        );

        assertEquals("Gość o identyfikatorze 1 nie został znaleziony.", exception.getMessage());
    }

    @Test
    void shouldUpdateTransport() {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        Guest guest = new Guest();
        guest.setId(1L);
        guest.setUser(user);

        when(guestRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(guest));

        guestService.updateTransport(1L, "Tak", user);

        assertEquals("Tak", guest.getTransport());

        verify(guestRepository).findByIdAndUser(1L, user);
        verifyNoMoreInteractions(guestRepository);
    }

    @Test
    void shouldUpdateLodging() {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        Guest guest = new Guest();
        guest.setId(1L);
        guest.setUser(user);

        when(guestRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(guest));

        guestService.updateLodging(1L, "Tak", user);

        assertEquals("Tak", guest.getAccommodation());
        verify(guestRepository).findByIdAndUser(1L, user);
        verify(guestRepository, never()).save(any());
    }

    @Test
    void shouldFindByConfirmedPresence() {
        User user = new User();
        when(guestRepository.findByUserAndAttendanceConfirmation(user, "tak"))
                .thenReturn(List.of(new Guest(), new Guest()));

        List<Guest> guests = guestService.findByConfirmedPresence(user, "tak");

        assertEquals(2, guests.size());
        verify(guestRepository).findByUserAndAttendanceConfirmation(user, "tak");
    }

    @Test
    void shouldFindByPoprawiny() {
        User user = new User();
        when(guestRepository.findByUserAndAfterParty(user, "tak"))
                .thenReturn(List.of(new Guest()));

        List<Guest> guests = guestService.findByPoprawiny(user, "tak");

        assertEquals(1, guests.size());
        verify(guestRepository).findByUserAndAfterParty(user, "tak");
    }

    @Test
    void shouldGetAllGuestsByUser() {
        User user = new User();
        when(guestRepository.findByUser(user))
                .thenReturn(List.of(new Guest()));

        List<Guest> guests = guestService.getAllGuestsByUser(user);

        assertEquals(1, guests.size());
        verify(guestRepository).findByUser(user);
    }

    @Test
    void shouldSaveGuest() {

        User user = new User();
        user.setId(1L);

        Guest guest = new Guest();
        guest.setFirstName("John");
        guest.setUser(user);

        when(guestRepository.save(any(Guest.class))).thenReturn(guest);

        Guest saved = guestService.saveGuest(guest);

        assertNotNull(saved);
        verify(guestRepository).save(guest);
    }
}