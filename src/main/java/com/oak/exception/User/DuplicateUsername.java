package com.oak.exception.User;

public class DuplicateUsername extends Exception{
    public DuplicateUsername(String username) {
        super("User with username " + username + " already exists");
    }
}
