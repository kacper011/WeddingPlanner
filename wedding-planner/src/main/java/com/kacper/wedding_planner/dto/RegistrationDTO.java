package com.kacper.wedding_planner.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationDTO {

    @NotBlank(message = "Email jest wymagany")
    @Email(message = "Nieprawidłowy format email")
    private String email;

    @NotBlank(message = "Imię jest wymagane")
    private String firstName;

    @NotBlank(message = "Hasło jest wymagane")
    @Size(min = 8, message = "Hasło musi mieć minimum 8 znaków")
    private String password;

    @NotBlank(message = "Potwierdzenie hasła jest wymagane")
    private String confirmPassword;
}
