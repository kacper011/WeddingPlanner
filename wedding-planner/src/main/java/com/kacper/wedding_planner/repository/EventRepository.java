package com.kacper.wedding_planner.repository;

import com.kacper.wedding_planner.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByUserEmail(String email);
}
