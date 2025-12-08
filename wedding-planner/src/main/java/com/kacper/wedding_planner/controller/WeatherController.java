package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.config.CustomUserDetails;
import com.kacper.wedding_planner.dto.WeatherResponse;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class WeatherController {

    private final UserService userService;

    public WeatherController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/guests/countdown/weather")
    @ResponseBody
    public ResponseEntity<WeatherResponse> getWeatherData(@AuthenticationPrincipal CustomUserDetails principal) {

        User user = userService.findByEmail(principal.getUsername());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie znaleziono użytkownika");
        }

        double latitude = 51.4255;
        double longitude = 23.0617;

        String url = "https://api.open-meteo.com/v1/forecast" +
                "?latitude=" + latitude +
                "&longitude=" + longitude +
                "&daily=temperature_2m_max,temperature_2m_min,precipitation_sum" +
                "&forecast_days=10" +
                "&timezone=auto";

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(5000);
        RestTemplate restTemplate = new RestTemplate(factory);

        WeatherResponse response;
        try {
            response = restTemplate.getForObject(url, WeatherResponse.class);
        } catch (RestClientException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }

        if (response == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }

        if (response.getDaily() == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }

        return ResponseEntity.ok(response);
    }
}
