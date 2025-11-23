package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.model.WeddingTask;
import com.kacper.wedding_planner.repository.WeddingTaskRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/checklist")
public class WeddingTaskController {

    private final WeddingTaskRepository weddingTaskRepository;

    public WeddingTaskController(WeddingTaskRepository weddingTaskRepository) {
        this.weddingTaskRepository = weddingTaskRepository;
    }

    @GetMapping
    public String checklist(Model model) {
        model.addAttribute("tasks", weddingTaskRepository.findAll());
        model.addAttribute("newTask", new WeddingTask());
        return "checklist";
    }
}
