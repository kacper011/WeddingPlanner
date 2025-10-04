package com.kacper.wedding_planner.exception;

public class InvalidExpenseDataException extends RuntimeException{

    public InvalidExpenseDataException() {
        super("Należy uzupełnić wszystkie pola wydatków (kwota i nazwa)");
    }
}
