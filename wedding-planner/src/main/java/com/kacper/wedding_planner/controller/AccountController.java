package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    private final UserService userService;

    @GetMapping
    public String showAccountPage(Model model) {

    }
}
