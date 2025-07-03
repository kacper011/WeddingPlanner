package com.kacper.wedding_planner.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/wedding_games")
public class WeddingGamesController {

    private List<String> questions;
    @PostConstruct
    public void init() throws IOException {
        ClassPathResource resource = new ClassPathResource("json_files/questions.json");
        ObjectMapper mapper = new ObjectMapper();
        Map<String, List<String>> data = mapper.readValue(resource.getInputStream(), Map.class);
        this.questions = data.get("questions");
        System.out.println("Loaded questions: " + questions.size());
    }

    @GetMapping
    public String weddingGamesPage(Model model) {
        model.addAttribute("questions", questions);
        return "wedding_games";
    }
}
