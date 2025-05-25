package com.kacper.wedding_planner.repository;

import com.kacper.wedding_planner.model.Guest;
import com.kacper.wedding_planner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {

    List<Guest> findAllByUser(User user);

    List<Guest> findByPotwierdzenieObecnosci(String potwierdzenieObecnosci);

    List<Guest> findByPoprawiny(String poprawiny);
}
