package com.kacper.wedding_planner.service.impl;

import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.model.WeddingTask;
import com.kacper.wedding_planner.repository.WeddingTaskRepository;
import com.kacper.wedding_planner.service.WeddingTaskService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeddingTaskServiceImpl implements WeddingTaskService {

    private final WeddingTaskRepository weddingTaskRepository;

    public WeddingTaskServiceImpl(WeddingTaskRepository weddingTaskRepository) {
        this.weddingTaskRepository = weddingTaskRepository;
    }

    @Override
    public List<WeddingTask> getForUser(User user) {
        return weddingTaskRepository.findByUser(user);
    }

    @Override
    public void save(WeddingTask task) {
        weddingTaskRepository.save(task);
    }

    @Override
    public void delete(Long id) {
        weddingTaskRepository.deleteById(id);
    }

    @Override
    public void toggle(Long id) {
        WeddingTask weddingTask = weddingTaskRepository.findById(id).orElseThrow();
        weddingTask.setCompleted(!weddingTask.isCompleted());
        weddingTaskRepository.save(weddingTask);
    }
}
