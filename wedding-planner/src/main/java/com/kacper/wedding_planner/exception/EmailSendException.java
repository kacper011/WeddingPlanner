package com.kacper.wedding_planner.exception;

import com.kacper.wedding_planner.service.EmailService;

public class EmailSendException extends RuntimeException {
    public EmailSendException(String message, Throwable cause) {
        super(message, cause);
    }
}
