package com.kacper.wedding_planner.service.impl;

import com.kacper.wedding_planner.exception.GuestTableNotFoundException;
import com.kacper.wedding_planner.model.Guest;
import com.kacper.wedding_planner.model.GuestTable;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.GuestRepository;
import com.kacper.wedding_planner.repository.GuestTableRepository;
import com.kacper.wedding_planner.service.GuestTableService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GuestTableServiceImpl implements GuestTableService {

    private final GuestTableRepository guestTableRepository;
    private final GuestRepository guestRepository;

    public GuestTableServiceImpl(GuestTableRepository guestTableRepository, GuestRepository guestRepository) {
        this.guestTableRepository = guestTableRepository;
        this.guestRepository = guestRepository;
    }

    @Override
    public List<GuestTable> getTablesForUser(User user) {
        return guestTableRepository.findByUser(user);
    }
    @Transactional
    public void detachGuestsFromTable(Long tableId, User user) {
        GuestTable table = guestTableRepository.findById(tableId)
                .orElseThrow(() -> new GuestTableNotFoundException(tableId));

        if (!table.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You are not authorized to modify this table");
        }

        List<Guest> guests = table.getGuests();
        if (guests != null && !guests.isEmpty()) {
            guests.forEach(guest -> guest.setTable(null));
            guestRepository.saveAll(guests);
        }
    }

    @Transactional
    public void deleteTableById(Long id, User user) {
        GuestTable table = guestTableRepository.findById(id)
                .orElseThrow(() -> new GuestTableNotFoundException(id));

        if (!table.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You are not authorized to modify this table");
        }

        detachGuestsFromTable(id, user);

        guestTableRepository.delete(table);
    }
}
