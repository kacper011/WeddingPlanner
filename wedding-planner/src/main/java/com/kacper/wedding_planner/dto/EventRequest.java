package com.kacper.wedding_planner.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EventRequest {

    @NotBlank(message = "Tytuł nie może być pusty")
    private String title;

    @NotNull(message = "Data jest wymagana")
    @FutureOrPresent(message = "Data nie może być przeszła")
    private LocalDate date;
}
