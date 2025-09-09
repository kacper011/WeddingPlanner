package com.kacper.wedding_planner.controller;

import com.kacper.wedding_planner.config.CustomUserDetails;
import com.kacper.wedding_planner.dto.WeatherResponse;
import com.kacper.wedding_planner.infrastructure.watherClient.WeatherClient;
import com.kacper.wedding_planner.model.User;
import com.kacper.wedding_planner.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@Controller
@RequiredArgsConstructor
public class WeatherController {

    private final UserService userService;
    private final WeatherClient weatherClient;

    @GetMapping("/guests/countdown/weather")
    @ResponseBody
    public WeatherResponse getWeatherData(Model model) {

        // założenie jest takie że para młoda wskazuje współrzędne gdzie będzie ślub/wesele i trzymasz tą informacje w tabeli
        // WeedingInfo i z łatwością możesz zmienić logike sprawdzania pogody z zahardkodowanego punktu 51.42, 23.06 na
        // dowolne miejsce a współrzędne przekazywać jako parametry endpointu
        double latitude = 51.4255;
        double longitude = 23.0617;

        return weatherClient.getWeatherData(latitude, longitude);
    }
}
