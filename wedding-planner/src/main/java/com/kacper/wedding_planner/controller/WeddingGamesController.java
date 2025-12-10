package com.kacper.wedding_planner.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/wedding_games")
public class WeddingGamesController {

    private static final Logger log = LoggerFactory.getLogger(WeddingGamesController.class);

    private List<String> questions = Collections.emptyList();

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("json_files/questions.json");
            ObjectMapper mapper = new ObjectMapper();

            Map<String, List<String>> data = mapper.readValue(
                    resource.getInputStream(),
                    new TypeReference<Map<String, List<String>>>() {}
            );

            this.questions = data.getOrDefault("questions", Collections.emptyList());
            log.info("Loaded questions: {}", questions.size());

        } catch (IOException e) {
            log.error("Error loading questions.json", e);
            this.questions = Collections.emptyList();
        }
    }

    @GetMapping
    public String weddingGamesPage(Model model) {
        model.addAttribute("questions", questions);
        return "wedding_games";
    }
}
