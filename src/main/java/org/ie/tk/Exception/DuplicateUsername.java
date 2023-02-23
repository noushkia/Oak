package org.ie.tk.Exception;

public class DuplicateUsername extends Exception{
    public DuplicateUsername(String username) {
        super("User with username " + username + " already exists");
    }
}
