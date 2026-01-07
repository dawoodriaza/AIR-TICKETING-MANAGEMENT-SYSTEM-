package com.ga.airticketmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InformationFoundException extends RuntimeException {
    public InformationFoundException() {
        super("Information already exists.");
    }

    public InformationFoundException(String message) {
        super(message);
    }

    public InformationFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public InformationFoundException(Throwable cause) {
        super(cause);
    }
}
