package com.ga.airticketmanagement.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ExpiredVerificationTokenException extends RuntimeException {
    private String email;
    public ExpiredVerificationTokenException(String email) {
        super("Verification token is expired");
        this.email = email;
    }
}
