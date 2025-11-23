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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
