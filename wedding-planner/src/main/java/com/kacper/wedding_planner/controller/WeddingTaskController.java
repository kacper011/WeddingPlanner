package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.config.CustomUserDetails;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.model.WeddingTask;
import com.kacper.wedding_planner.repository.WeddingTaskRepository;
import com.kacper.wedding_planner.service.UserService;
import com.kacper.wedding_planner.service.WeddingTaskService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/checklist")
public class WeddingTaskController {

    private final WeddingTaskService weddingTaskService;
    private final UserService userService;


    public WeddingTaskController(WeddingTaskService weddingTaskService, UserService userService) {
        this.weddingTaskService = weddingTaskService;
        this.userService = userService;
    }

    @GetMapping
    public String showChecklist(@AuthenticationPrincipal CustomUserDetails principal, Model model) {

        User user = userService.findByEmail(principal.getUsername());

        model.addAttribute("tasks", weddingTaskService.getForUser(user));
        model.addAttribute("newTask", new WeddingTask());

        return "checklist";
    }

    @PostMapping
    public String addTask(@ModelAttribute("newTask") WeddingTask task,
                          @AuthenticationPrincipal CustomUserDetails principal) {

        User user = userService.findByEmail(principal.getUsername());
        task.setUser(user);

        weddingTaskService.save(task);

        return "redirect:/checklist";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id) {

        weddingTaskService.delete(id);
        return "redirect:/checklist";
    }

    @PostMapping("/toggle/{id}")
    public String toggle(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails principal) {

        User user = userService.findByEmail(principal.getUsername());
        weddingTaskService.toggleForUser(id, user);

        return "redirect:/checklist";
    }
}
