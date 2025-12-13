package com.kacper.wedding_planner.service;

import com.kacper.wedding_planner.model.Guest;
import com.kacper.wedding_planner.model.User;

import java.util.List;

public interface GuestService {
    void deleteGuest(Long id, User user);
    void updatePresence(Long id, String presence, User user);
    void updateTransport(Long id, String transport, User user);
    void updateLodging(Long id, String lodging, User user);
    List<Guest> findByConfirmedPresence(User user, String attendanceConfirmation);
    List<Guest> findByPoprawiny(User user, String afterParty);
    List<Guest> getAllGuestsByUser(User user);
    Guest saveGuest(Guest guest);
}
