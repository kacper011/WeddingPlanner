package com.kacper.wedding_planner.repository;

import com.kacper.wedding_planner.model.Guest;
import com.kacper.wedding_planner.model.GuestCategory;
import com.kacper.wedding_planner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {

    List<Guest> findByUser(User user);
    List<Guest> findByUserAndAttendanceConfirmation(User user, String status);
    List<Guest> findByUserAndAfterParty(User user, String afterParty);
    List<Guest> findAllByCategory(GuestCategory category);
}
