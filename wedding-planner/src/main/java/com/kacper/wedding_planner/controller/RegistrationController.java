package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.dto.RegistrationDTO;
import com.kacper.wedding_planner.dto.UserDTO;
import com.kacper.wedding_planner.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationDto", new RegistrationDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("registrationDto") RegistrationDTO dto, Model model) {

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            model.addAttribute("error", "The passwords are not the same!");
            return "register";
        }

        try {
            userService.registerUser(dto.getEmail(), dto.getPassword(), dto.getFirstName());
            return "redirect:/login?success";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}
