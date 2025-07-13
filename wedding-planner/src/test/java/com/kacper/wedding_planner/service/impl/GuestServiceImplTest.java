package com.kacper.wedding_planner.service.impl;

import com.kacper.wedding_planner.model.Guest;
import com.kacper.wedding_planner.repository.GuestRepository;
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
}