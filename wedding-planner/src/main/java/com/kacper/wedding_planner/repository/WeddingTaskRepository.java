package com.kacper.wedding_planner.repository;

import com.kacper.wedding_planner.model.WeddingTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeddingTaskRepository extends JpaRepository<WeddingTask, Long> {
}
