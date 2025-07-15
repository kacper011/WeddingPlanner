package com.kacper.wedding_planner.service.impl;

import com.kacper.wedding_planner.model.GuestTable;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.GuestRepository;
import com.kacper.wedding_planner.repository.GuestTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

}