package com.oak.exception.User;

public class InvalidCredentials extends Exception {
    public InvalidCredentials() {
        super("Wrong password!");
    }
}
