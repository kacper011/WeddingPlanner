package com.kacper.wedding_planner.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Nieprawidłowy login lub hasło");
        }
        if (logout != null) {
            model.addAttribute("msg", "Zostałeś wylogowany pomyślnie");
        }
        return "login";
    }
}