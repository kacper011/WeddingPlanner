package com.kacper.wedding_planner.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeDTO {

    @NotBlank(message = "Podaj stare hasło.")
    private String oldPassword;

    @NotBlank(message = "Podaj nowe hasło.")
    private String newPassword;

    @NotBlank(message = "Powtórz nowe hasło.")
    private String confirmPassword;
}
