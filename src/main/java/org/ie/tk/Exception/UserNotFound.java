package org.ie.tk.Exception;

public class UserNotFound extends Exception{
    public UserNotFound(String username) {
        super("No provider with username " + username + " found");
    }
}
