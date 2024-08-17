package com.kacper.wedding_planner.service.impl;

import com.kacper.wedding_planner.model.Guest;
import com.kacper.wedding_planner.repository.GuestRepository;
import com.kacper.wedding_planner.service.GuestService;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public Guest saveGuest(Guest guest) {

        return guestRepository.save(guest);
    }


}
