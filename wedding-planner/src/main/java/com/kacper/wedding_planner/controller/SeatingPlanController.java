package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.config.CustomUserDetails;
import com.kacper.wedding_planner.model.GuestTable;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.repository.UserRepository;
import com.kacper.wedding_planner.service.GuestTableService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/tables")
public class SeatingPlanController {
    private final GuestTableService guestTableService;
    private final UserRepository userRepository;

    public SeatingPlanController(GuestTableService guestTableService, UserRepository userRepository) {
        this.guestTableService = guestTableService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String showSeatingPlan(@AuthenticationPrincipal CustomUserDetails principal, Model model) {

        User user = userRepository.findByEmail(principal.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));


        List<GuestTable> tables = guestTableService.getTableForUser(user);
        model.addAttribute("stoly", tables);
        return "tables";
    }
}
