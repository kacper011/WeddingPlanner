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
        Guest guest = guestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guest not found"));
        guest.setPotwierdzenieObecnosci(presence);
        guestRepository.save(guest);
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
    public List<Guest> findByConfirmedPresence(String potwierdzenieObecnosci) {

        return guestRepository.findByPotwierdzenieObecnosci(potwierdzenieObecnosci);
    }

    @Override
    public List<Guest> findByPoprawiny(String poprawiny) {
        return guestRepository.findByPoprawiny(poprawiny);
    }


    @Override
    public Guest saveGuest(Guest guest) {

        return guestRepository.save(guest);
    }


}
