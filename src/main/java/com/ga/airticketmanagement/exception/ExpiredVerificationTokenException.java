package com.ga.airticketmanagement.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@Getter
public class ExpiredVerificationTokenException extends RuntimeException {
    private String email;
    public ExpiredVerificationTokenException(String email) {
        super("Verification token is expired");
        this.email = email;
    }
}
