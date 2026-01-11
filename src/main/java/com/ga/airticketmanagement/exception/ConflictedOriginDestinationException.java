package com.ga.airticketmanagement.exception;

public class ConflictedOriginDestinationException extends RuntimeException {
    public ConflictedOriginDestinationException(String message){
        super(message);
    }
}
