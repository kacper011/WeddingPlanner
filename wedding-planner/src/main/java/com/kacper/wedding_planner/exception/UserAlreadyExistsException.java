package com.kacper.wedding_planner.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email) {
        super("Użytkownik z podanym adresem e-mail już istnieje.");
    }
}
