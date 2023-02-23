package org.ie.tk.Exception;

public class InvalidUsername extends Exception{
    public InvalidUsername() {
        super("Username must only consist of letters, digits and underscore");
    }
}
