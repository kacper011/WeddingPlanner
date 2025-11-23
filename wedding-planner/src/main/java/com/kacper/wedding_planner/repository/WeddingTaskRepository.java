package com.kacper.wedding_planner.repository;

import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.model.WeddingTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeddingTaskRepository extends JpaRepository<WeddingTask, Long> {

    List<WeddingTask> findByUser(User user);
}
