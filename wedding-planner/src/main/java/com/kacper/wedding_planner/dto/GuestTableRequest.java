package com.kacper.wedding_planner.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestTableRequest {

    @NotBlank
    @Size(max = 50)
    private String name;
    @NotNull
    @Min(0)
    @Max(5000)
    private Integer positionX = 100;
    @NotNull
    @Min(0)
    @Max(5000)
    private Integer positionY = 100;
    @NotNull
    @Pattern(regexp = "circle|rectangle")
    private String shape;
}
