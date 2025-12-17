package com.kacper.wedding_planner.repository;

import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.model.WeddingTimeline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WeddingTimelineRepository
        extends JpaRepository<WeddingTimeline, Long> {

    List<WeddingTimeline> findByUserOrderByTimeAsc(User user);

    Optional<WeddingTimeline> findByIdAndUser(Long id, User user);

    void deleteByIdAndUser(Long id, User user);
}

