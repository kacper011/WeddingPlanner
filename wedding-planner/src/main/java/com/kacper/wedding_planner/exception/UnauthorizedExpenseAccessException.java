package com.kacper.wedding_planner.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedExpenseAccessException extends RuntimeException{

    public UnauthorizedExpenseAccessException() {
        super("Nie jesteś autoryzowany do usunięcia tego wydatku.");
    }
}
