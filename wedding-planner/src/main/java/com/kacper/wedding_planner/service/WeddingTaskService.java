package com.kacper.wedding_planner.service;

import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.model.WeddingTask;

import java.util.List;

public interface WeddingTaskService {

    List<WeddingTask> getForUser(User user);

    void save(WeddingTask task);

    void delete(Long id);

    void toggleForUser(Long id, User user);
}
