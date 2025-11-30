package com.kacper.wedding_planner.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ExpenseRequest {
    @NotNull(message = "Nazwa jest wymagana")
    @Size(min = 1, message = "Nazwa nie może być pusta")
    private String name;

    @NotNull(message = "Kwota jest wymagana")
    @DecimalMin(value = "0.01", message = "Kwota musi być większa od zera")
    private BigDecimal amount;
}
