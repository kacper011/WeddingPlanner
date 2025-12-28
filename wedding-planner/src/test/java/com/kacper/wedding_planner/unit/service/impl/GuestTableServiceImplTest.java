package com.kacper.wedding_planner.unit.service.impl;

import com.kacper.wedding_planner.exception.GuestTableNotFoundException;
import com.kacper.wedding_planner.model.Guest;
import com.kacper.wedding_planner.model.GuestTable;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.GuestRepository;
import com.kacper.wedding_planner.repository.GuestTableRepository;
import com.kacper.wedding_planner.service.impl.GuestTableServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class GuestTableServiceImplTest {
    @Mock
    private GuestTableRepository guestTableRepository;
    @Mock
    private GuestRepository guestRepository;
    @InjectMocks
    private GuestTableServiceImpl guestTableService;

    @Test
    void shouldReturnTablesForUser() {
        User user = new User();
        List<GuestTable> tables = List.of(new GuestTable(), new GuestTable());

        when(guestTableRepository.findByUser(user)).thenReturn(tables);

        List<GuestTable> result = guestTableService.getTablesForUser(user);

        assertEquals(2, result.size());
        verify(guestTableRepository).findByUser(user);
    }

    @Test
    void shouldDetachGuestsFromTable() {
        Long tableId = 1L;

        User user = new User();
        user.setId(10L);

        GuestTable table = new GuestTable();
        table.setId(tableId);
        table.setUser(user);

        Guest guest1 = new Guest();
        guest1.setId(1L);
        guest1.setTable(table);

        Guest guest2 = new Guest();
        guest2.setId(2L);
        guest2.setTable(table);

        table.setGuests(Arrays.asList(guest1, guest2));

        when(guestTableRepository.findById(tableId)).thenReturn(Optional.of(table));

        guestTableService.detachGuestsFromTable(tableId, user);

        assertNull(guest1.getTable());
        assertNull(guest2.getTable());

        verify(guestRepository).saveAll(anyList());
    }

    @Test
    void shouldThrowExceptionIfTableNotFoundWhenDetachingGuests() {
        Long tableId = 1L;

        User user = new User();

        when(guestTableRepository.findById(tableId)).thenReturn(Optional.empty());

        GuestTableNotFoundException exception =
                assertThrows(GuestTableNotFoundException.class, () ->
                        guestTableService.detachGuestsFromTable(tableId, user));;

        assertEquals("Table with ID: " + tableId + " not found.", exception.getMessage());
    }

    @Test
    void shouldDeleteTableAndDetachGuests() {
        Long tableId = 1L;

        User user = new User();
        user.setId(100L);

        Guest guest = new Guest();
        guest.setId(1L);

        GuestTable table = new GuestTable();
        table.setId(tableId);
        table.setGuests(List.of(guest));
        table.setUser(user);

        guest.setTable(table);

        when(guestTableRepository.findById(tableId)).thenReturn(Optional.of(table));

        guestTableService.deleteTableById(tableId, user);

        assertNull(guest.getTable(), "Guest should be detached from table");

        verify(guestRepository).saveAll(anyList());
        verify(guestTableRepository).delete(table);
    }

    @Test
    void shouldThrowSecurityExceptionWhenUserIsNotOwner() {

        Long tableId = 1L;

        User owner = new User();
        owner.setId(1L);

        User attacker = new User();
        attacker.setId(2L);

        GuestTable table = new GuestTable();
        table.setId(tableId);
        table.setUser(owner);

        when(guestTableRepository.findById(tableId)).thenReturn(Optional.of(table));

        assertThrows(SecurityException.class, () -> guestTableService.detachGuestsFromTable(tableId, attacker));
    }


}