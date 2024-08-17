package com.kacper.wedding_planner.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WeddingController {

    @GetMapping("")
    public String showHomePage() {
        return "home_page";
    }
}
