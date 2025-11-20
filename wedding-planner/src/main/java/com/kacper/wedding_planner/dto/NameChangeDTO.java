package com.kacper.wedding_planner.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NameChangeDTO {

    @NotBlank(message = "Imię nie może być puste")
    private String firstName;
}
