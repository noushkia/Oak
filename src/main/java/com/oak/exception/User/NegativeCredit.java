package com.oak.exception.User;

public class NegativeCredit extends Exception{
    public NegativeCredit() {
        super("Credit must be greater or equal to zero");
    }
}
