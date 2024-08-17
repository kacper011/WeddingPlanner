package com.kacper.wedding_planner.service;

import com.kacper.wedding_planner.model.Guest;

import java.util.List;

public interface GuestService {
    Guest saveGuest(Guest guest);

    List<Guest> getAllGuests();
}
