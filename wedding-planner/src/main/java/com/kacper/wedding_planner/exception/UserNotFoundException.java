package com.kacper.wedding_planner.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String email) {
        super("Nie znaleziono użytkownika.");
    }
}
