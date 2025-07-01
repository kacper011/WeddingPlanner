package com.kacper.wedding_planner.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class EventDTO {
    private Long id;
    private String title;
    private LocalDate date;


}
