package com.kacper.wedding_planner.service;

import com.kacper.wedding_planner.model.Guest;

import java.util.List;

public interface GuestService {
    Guest saveGuest(Guest guest);

    List<Guest> getAllGuests();

    void deleteGuest(Long id);

    void updatePresence(Long id, String presence);

    void updateTransport(Long id, String transport);

    void updateLodging(Long id, String lodging);

    List<Guest> findByConfirmedPresence(String potwierdzenieObecnosci);


}
