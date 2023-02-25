package org.ie.tk.Exception.User;

public class UserNotFound extends Exception{
    public UserNotFound(String username) {
        super("No user with username " + username + " found");
    }
}
