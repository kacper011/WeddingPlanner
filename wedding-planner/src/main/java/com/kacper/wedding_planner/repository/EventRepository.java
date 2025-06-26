package com.kacper.wedding_planner.repository;

import com.kacper.wedding_planner.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByUserEmail(String email);

    @Query("SELECT e FROM Event e WHERE e.date = :targetDate")
    List<Event> findEventsOnDate(@Param("targetDate")LocalDate targetDate);

    List<Event> findByDate(LocalDate date);

    List<Event> findByDateAndReminderSentFalse(LocalDate targetDate);
}
