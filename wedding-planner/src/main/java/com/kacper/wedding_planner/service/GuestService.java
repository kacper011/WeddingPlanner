package com.kacper.wedding_planner.service;

import com.kacper.wedding_planner.model.Guest;
import com.kacper.wedding_planner.model.User;

import java.util.List;

public interface GuestService {
    Guest saveGuest(Guest guest);

    List<Guest> getAllGuestsByUser(User user);

    void deleteGuest(Long id);

    void updatePresence(Long id, String presence);

    void updateTransport(Long id, String transport);

    void updateLodging(Long id, String lodging);

    List<Guest> findByConfirmedPresence(User user, String attendanceConfirmation);

    List<Guest> findByPoprawiny(User user, String afterParty);



}
