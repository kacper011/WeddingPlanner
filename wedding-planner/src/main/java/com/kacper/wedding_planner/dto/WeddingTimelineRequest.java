package com.kacper.wedding_planner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class WeddingTimelineRequest {

    @NotBlank(message = "Tytuł nie może być pusty")
    private String title;
    @NotNull(message = "Godzina jest obowiązkowa")
    private LocalTime time;
}
