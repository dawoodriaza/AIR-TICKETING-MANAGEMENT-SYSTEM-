package com.ga.airticketmanagement.exceptions;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class EmailUsedException extends RuntimeException {

    public EmailUsedException(String message) {
        super(message);
    }
}
