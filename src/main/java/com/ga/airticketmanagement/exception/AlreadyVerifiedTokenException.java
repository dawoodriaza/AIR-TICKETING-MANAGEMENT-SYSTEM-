package com.ga.airticketmanagement.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AlreadyVerifiedTokenException extends RuntimeException {

    public AlreadyVerifiedTokenException(String message) {
        super(message);
    }
}
