package com.kacper.wedding_planner.service.impl;

import com.kacper.wedding_planner.model.Guest;
import com.kacper.wedding_planner.repository.GuestRepository;
import com.kacper.wedding_planner.service.GuestService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GuestServiceImpl implements GuestService {

    private final GuestRepository guestRepository;

    public GuestServiceImpl(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    @Override
    public List<Guest> getAllGuests() {
        List<Guest> guests = guestRepository.findAll();
        return guests;
    }

    @Override
    public void deleteGuest(Long id) {
        guestRepository.deleteById(id);
    }

    @Override
    public void updatePresence(Long id, String presence) {
        Optional<Guest> guestOptional = guestRepository.findById(id);
        if (guestOptional.isPresent()) {
            Guest guest = guestOptional.get();
            guest.setPotwierdzenieObecnosci(presence);
            guestRepository.save(guest);
            System.out.println("Updated guest with id: " + id + " to presence: " + presence);
        } else {
            System.out.println("Guest with id: " + id + " not found.");
        }
    }

    @Override
    public void updateTransport(Long id, String transport) {
        Optional<Guest> guestOptional = guestRepository.findById(id);
        if (guestOptional.isPresent()) {
            Guest guest = guestOptional.get();
            guest.setTransport(transport);
            guestRepository.save(guest);
        }
    }

    @Override
    public void updateLodging(Long id, String lodging) {
        Optional<Guest> guestOptional = guestRepository.findById(id);
        if (guestOptional.isPresent()) {
            Guest guest = guestOptional.get();
            guest.setNocleg(lodging);
            guestRepository.save(guest);
        }
    }

    @Override
    public Guest saveGuest(Guest guest) {

        return guestRepository.save(guest);
    }


}
