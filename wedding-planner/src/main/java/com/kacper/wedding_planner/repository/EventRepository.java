package com.kacper.wedding_planner.repository;

import com.kacper.wedding_planner.model.Event;
import com.kacper.wedding_planner.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByUser(User user);

    List<Event> findByDate(LocalDate date);

    List<Event> findByDateAndReminderSentFalse(LocalDate targetDate);
}
