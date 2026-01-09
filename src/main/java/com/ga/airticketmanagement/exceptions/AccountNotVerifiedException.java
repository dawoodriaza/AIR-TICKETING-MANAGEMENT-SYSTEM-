package com.ga.airticketmanagement.exceptions;

public class AccountNotVerifiedException extends RuntimeException {
    public AccountNotVerifiedException(){
        super("Account is not verified");
    }
}
