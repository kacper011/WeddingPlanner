package com.kacper.wedding_planner.unit.service.impl;

import com.kacper.wedding_planner.model.Guest;
import com.kacper.wedding_planner.model.GuestTable;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.GuestRepository;
import com.kacper.wedding_planner.repository.GuestTableRepository;
import com.kacper.wedding_planner.service.impl.GuestTableServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GuestTableServiceImplTest {

    private GuestTableRepository guestTableRepository;
    private GuestRepository guestRepository;
    private GuestTableServiceImpl guestTableService;

    @BeforeEach
    void setUp() {
        guestTableRepository = mock(GuestTableRepository.class);
        guestRepository = mock(GuestRepository.class);
        guestTableService = new GuestTableServiceImpl(guestTableRepository, guestRepository);
    }

    @Test
    void shouldReturnTablesForUser() {
        User user = new User();
        List<GuestTable> tables = List.of(new GuestTable(), new GuestTable());

        when(guestTableRepository.findByUser(user)).thenReturn(tables);

        List<GuestTable> result = guestTableService.getTableForUser(user);

        assertEquals(2, result.size());
        verify(guestTableRepository).findByUser(user);
    }

    @Test
    void shouldDetachGuestsFromTable() {
        Long tableId = 1L;

        Guest guest1 = new Guest();
        guest1.setId(1L);

        Guest guest2 = new Guest();
        guest2.setId(2L);

        GuestTable table = new GuestTable();
        table.setId(tableId);
        table.setGoscie(Arrays.asList(guest1, guest2));

        when(guestTableRepository.findById(tableId)).thenReturn(Optional.of(table));

        guestTableService.detachGuestsFromTable(tableId);

        assertNull(guest1.getTable());
        assertNull(guest2.getTable());

        verify(guestRepository, times(2)).save(any(Guest.class));
        verify(guestRepository).flush();
    }

    @Test
    void shouldThrowExceptionIfTableNotFoundWhenDetachingGuests() {
        Long tableId = 1L;
        when(guestTableRepository.findById(tableId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            guestTableService.detachGuestsFromTable(tableId);
        });

        assertEquals("Nie znaleziono stołu o id: " + tableId, exception.getMessage());
    }

    @Test
    void shouldDeleteTableAndDetachGuests() {
        Long tableId = 1L;

        Guest guest = new Guest();
        guest.setId(1L);
        GuestTable table = new GuestTable();
        table.setId(tableId);
        table.setGoscie(List.of(guest));
        guest.setTable(table);

        when(guestTableRepository.findById(tableId)).thenReturn(Optional.of(table));

        guestTableService.deleteTableById(tableId);

        assertNull(guest.getTable(), "Guest should be detached from table");

        verify(guestRepository).save(guest);
        verify(guestRepository).flush();
        verify(guestTableRepository).deleteById(tableId);
    }


}