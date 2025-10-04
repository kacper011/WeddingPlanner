package com.kacper.wedding_planner.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedExpenseAccessException extends RuntimeException{

    public UnauthorizedExpenseAccessException(String email) {
        super("Użytkownik " + email + " nie jest uprawniony do usunięcia tego wydatku.");
    }
}
