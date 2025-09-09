package com.kacper.wedding_planner.infrastructure.watherClient;

import com.kacper.wedding_planner.dto.WeatherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

@Slf4j
@RequiredArgsConstructor
public class WeatherClient {

    private final RestClient weatherClient;

    public WeatherResponse getWeatherData(double latitude, double longitude) {
        log.info("Retrieving weather data for latitude: {}, longitude: {}", latitude, longitude);

        return weatherClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("latitude", latitude)
                        .queryParam("longitude", longitude)
                        .queryParam("daily", "temperature_2m_max,temperature_2m_min,precipitation_sum")
                        .queryParam("forecast_days", 10)
                        .queryParam("timezone", "auto")
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
