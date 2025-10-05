package com.kacper.wedding_planner.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class GuestTableNotFoundException extends RuntimeException{

    public GuestTableNotFoundException(Long id) {
        super("Nie znaleziono stołu o id: " + id + ".");
    }
}
