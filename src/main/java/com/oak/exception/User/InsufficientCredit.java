package com.oak.exception.User;

public class InsufficientCredit extends Exception{

    public InsufficientCredit() {
        super("Not enough credit");
    }
}
