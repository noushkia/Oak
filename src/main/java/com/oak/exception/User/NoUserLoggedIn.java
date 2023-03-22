package com.oak.exception.User;

public class NoUserLoggedIn extends Exception{
    public NoUserLoggedIn() {
        super("No user is logged in!");
    }
}
