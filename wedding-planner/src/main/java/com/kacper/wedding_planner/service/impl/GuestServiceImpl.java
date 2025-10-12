package com.kacper.wedding_planner.service.impl;

import com.kacper.wedding_planner.exception.GuestNotFoundException;
import com.kacper.wedding_planner.model.Guest;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.GuestRepository;
import com.kacper.wedding_planner.service.GuestService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GuestServiceImpl implements GuestService {

    private final GuestRepository guestRepository;

    public GuestServiceImpl(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    @Transactional
    @Override
    public void deleteGuest(Long id) {
        guestRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void updatePresence(Long id, String presence) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new GuestNotFoundException(id));
        guest.setAttendanceConfirmation(presence);
        guestRepository.save(guest);
    }

    @Transactional
    @Override
    public void updateTransport(Long id, String transport) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new GuestNotFoundException(id));
        guest.setTransport(transport);
        guestRepository.save(guest);
    }

    @Transactional
    @Override
    public void updateLodging(Long id, String lodging) {
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new GuestNotFoundException(id));
        guest.setAccommodation(lodging);
        guestRepository.save(guest);
    }

    @Override
    public List<Guest> findByConfirmedPresence(User user, String potwierdzenieObecnosci) {
        return guestRepository.findByUserAndPotwierdzenieObecnosci(user, potwierdzenieObecnosci);
    }

    @Override
    public List<Guest> findByPoprawiny(User user, String poprawiny) {
        return guestRepository.findByUserAndPoprawiny(user, poprawiny);
    }

    @Override
    public List<Guest> getAllGuestsByUser(User user) {
        return guestRepository.findByUser(user);
    }


    @Transactional
    @Override
    public Guest saveGuest(Guest guest) {

        return guestRepository.save(guest);
    }


}
