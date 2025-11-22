package com.kacper.wedding_planner.repository;

import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.model.WeddingTimeline;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeddingTimelineRepository extends JpaRepository<WeddingTimeline, Long> {
    List<WeddingTimeline> findByUserOrderByTimeAsc(User user);
}
