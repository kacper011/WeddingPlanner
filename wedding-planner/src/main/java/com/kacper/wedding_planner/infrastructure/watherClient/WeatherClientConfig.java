package com.kacper.wedding_planner.infrastructure.watherClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;


@Configuration
class WeatherClientConfig {

    @Bean
    WeatherClient weatherClient() {
        var restClient = RestClient.builder()
                .baseUrl("https://api.open-meteo.com/v1/forecast")
                .build();

        return new WeatherClient(restClient);
    }
}
