package com.kacper.wedding_planner.unit.service.impl;

import com.kacper.wedding_planner.model.Guest;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.GuestRepository;
import com.kacper.wedding_planner.service.impl.GuestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void shouldReturnAllGuests() {
        List<Guest> guests = Arrays.asList(new Guest(), new Guest());
        when(guestRepository.findAll()).thenReturn(guests);

        List<Guest> result = guestService.getAllGuests();

        assertEquals(2, result.size());
        verify(guestRepository).findAll();
    }

    @Test
    void shouldDeleteGuestById() {
        guestService.deleteGuest(1L);
        verify(guestRepository).deleteById(1L);
    }

    @Test
    void shouldUpdatePresence() {
        Guest guest = new Guest();
        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest));

        guestService.updatePresence(1L, "tak");

        assertEquals("tak", guest.getPotwierdzenieObecnosci());
        verify(guestRepository).save(guest);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingPresenceAndGuestNotFound() {
        when(guestRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                guestService.updatePresence(1L, "tak"));

        assertEquals("Guest not found", exception.getMessage());
    }

    @Test
    void shouldUpdateTransport() {
        Guest guest = new Guest();
        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest));

        guestService.updateTransport(1L, "Tak");

        assertEquals("Tak", guest.getTransport());
        verify(guestRepository).save(guest);
    }

    @Test
    void shouldUpdateLodging() {
        Guest guest = new Guest();
        when(guestRepository.findById(1L)).thenReturn(Optional.of(guest));

        guestService.updateLodging(1L, "Tak");

        assertEquals("Tak", guest.getNocleg());
        verify(guestRepository).save(guest);
    }

    @Test
    void shouldFindByConfirmedPresence() {
        User user = new User();
        when(guestRepository.findByUserAndPotwierdzenieObecnosci(user, "tak"))
                .thenReturn(List.of(new Guest(), new Guest()));

        List<Guest> guests = guestService.findByConfirmedPresence(user, "tak");

        assertEquals(2, guests.size());
        verify(guestRepository).findByUserAndPotwierdzenieObecnosci(user, "tak");
    }

    @Test
    void shouldFindByPoprawiny() {
        User user = new User();
        when(guestRepository.findByUserAndPoprawiny(user, "tak"))
                .thenReturn(List.of(new Guest()));

        List<Guest> guests = guestService.findByPoprawiny(user, "tak");

        assertEquals(1, guests.size());
        verify(guestRepository).findByUserAndPoprawiny(user, "tak");
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
        Guest guest = new Guest();
        when(guestRepository.save(guest)).thenReturn(guest);

        Guest saved = guestService.saveGuest(guest);

        assertEquals(guest, saved);
        verify(guestRepository).save(guest);
    }
}