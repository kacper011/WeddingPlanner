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
    public void deleteGuest(Long id, User user) {
        Guest guest = guestRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new GuestNotFoundException(id));

        guestRepository.delete(guest);
    }

    @Transactional
    @Override
    public void updatePresence(Long id, String presence, User user) {
        Guest guest = guestRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new GuestNotFoundException(id));

        guest.setAttendanceConfirmation(presence);
    }

    @Transactional
    @Override
    public void updateTransport(Long id, String transport, User user) {
        Guest guest = guestRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new GuestNotFoundException(id));

        guest.setTransport(transport);
    }

    @Transactional
    @Override
    public void updateLodging(Long id, String lodging, User user) {
        Guest guest = guestRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new GuestNotFoundException(id));

        guest.setAccommodation(lodging);
    }

    @Override
    public List<Guest> findByConfirmedPresence(User user, String attendanceConfirmation) {
        return guestRepository.findByUserAndAttendanceConfirmation(user, attendanceConfirmation);
    }

    @Override
    public List<Guest> findByPoprawiny(User user, String afterParty) {
        return guestRepository.findByUserAndAfterParty(user, afterParty);
    }

    @Override
    public List<Guest> getAllGuestsByUser(User user) {
        return guestRepository.findByUser(user);
    }

    @Transactional
    @Override
    public Guest saveGuest(Guest guest) {
        if (guest.getUser() == null) {
            throw new IllegalStateException("Gość musi być przypisany do użytkownika");
        }
        return guestRepository.save(guest);
    }
}
