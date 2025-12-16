package com.kacper.wedding_planner.exception;

public class GuestNotFoundException extends RuntimeException {

    public GuestNotFoundException(Long id) {
        super("Gość o identyfikatorze " + id + " nie został znaleziony.");
    }
}
