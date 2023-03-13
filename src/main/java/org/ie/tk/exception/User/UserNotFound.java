package org.ie.tk.exception.User;

public class UserNotFound extends Exception{

    public UserNotFound(String username) {
        super("No user with username " + username + " found");
    }
}
