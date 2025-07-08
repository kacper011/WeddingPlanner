package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.config.CustomUserDetails;
import com.kacper.wedding_planner.dto.WeatherResponse;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@Controller
public class WeatherController {

    private final UserService userService;

    public WeatherController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/guests/countdown/weather")
    @ResponseBody
    public WeatherResponse getWeatherData(@AuthenticationPrincipal CustomUserDetails principal, Model model) {

        User user = userService.findByEmail(principal.getUsername());
        double latitude = 51.4255;
        double longitude = 23.0617;

        String url = "https://api.open-meteo.com/v1/forecast" +
                "?latitude=" + latitude +
                "&longitude=" + longitude +
                "&daily=temperature_2m_max,temperature_2m_min,precipitation_sum" +
                "&forecast_days=10" +
                "&timezone=auto";

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, WeatherResponse.class);

    }
}
