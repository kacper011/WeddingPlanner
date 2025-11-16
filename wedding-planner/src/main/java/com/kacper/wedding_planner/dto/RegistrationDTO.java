package com.kacper.wedding_planner.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationDTO {
    private String email;
    private String firstName;
    private String password;
    private String confirmPassword;
}
