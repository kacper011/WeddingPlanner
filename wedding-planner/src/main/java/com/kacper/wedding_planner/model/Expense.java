package com.kacper.wedding_planner.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wydatki")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Nazwa jest wymagana")
    @Size(min = 1, message = "Nazwa nie może być pusta")
    private String nazwa;
    @NotNull(message = "Kwota jest wymagana")
    @DecimalMin(value = "0.01", message = "Kwota musi być większa od zera")
    private BigDecimal kwota;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
