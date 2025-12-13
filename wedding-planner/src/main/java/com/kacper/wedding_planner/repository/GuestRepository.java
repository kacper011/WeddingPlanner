package com.kacper.wedding_planner.repository;

import com.kacper.wedding_planner.model.Guest;
import com.kacper.wedding_planner.model.GuestCategory;
import com.kacper.wedding_planner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {

    List<Guest> findByUser(User user);
    List<Guest> findByUserAndAttendanceConfirmation(User user, String status);
    List<Guest> findByUserAndAfterParty(User user, String afterParty);
    List<Guest> findAllByCategory(GuestCategory category);
    List<Guest> findByUserAndCategory(User user, GuestCategory category);
    List<Guest> findByUserAndLastNameContainingIgnoreCase(User user, String lastName);

    Optional<Guest> findByIdAndUser(Long id, User user);
}
