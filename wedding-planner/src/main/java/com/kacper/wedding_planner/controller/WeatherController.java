package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.config.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/weather")
public class WeatherController {

    @GetMapping
    public String showWeather(@AuthenticationPrincipal CustomUserDetails principal, Model model) {

        double latitude = 51.4255;
        double longitude = 23.0617;

        String url = "https://api.open-meteo.com/v1/forecast" +
                "?latitude=" + latitude +
                "&longitude=" + longitude +
                "&daily=temperature_2m_max,temperature_2m_min,precipitation_sum" +
                "&timezone=Europe%2FWarsaw";

        RestTemplate restTemplate = new RestTemplate();
        WeatherResponse response
    }
}
