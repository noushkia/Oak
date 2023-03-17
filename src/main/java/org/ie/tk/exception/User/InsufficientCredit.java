package org.ie.tk.exception.User;

public class InsufficientCredit extends Exception{

    public InsufficientCredit() {
        super("Not enough credit");
    }
}
