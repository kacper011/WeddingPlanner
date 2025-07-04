package com.kacper.wedding_planner.service.impl;

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
    public List<GuestTable> getTableForUser(User user) {
        return guestTableRepository.findByUser(user);
    }

    @Transactional
    public void detachGuestsFromTable(Long tableId) {
        GuestTable table = guestTableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono stołu o id: " + tableId));

        List<Guest> guests = table.getGoscie();
        if (guests != null) {
            for (Guest guest : guests) {
                guest.setTable(null);
                guestRepository.save(guest);
            }
            guestRepository.flush();
        }
    }

    @Transactional
    public void deleteTableById(Long id) {
        detachGuestsFromTable(id);
        guestTableRepository.deleteById(id);
    }
}
