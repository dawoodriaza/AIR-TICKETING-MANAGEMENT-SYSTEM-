package com.ga.airticketmanagement.exception;

public class AccountNotVerifiedException extends RuntimeException {
    public AccountNotVerifiedException(){
        super("Account is not verified");
    }
}
