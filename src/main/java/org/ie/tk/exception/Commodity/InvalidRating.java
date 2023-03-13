package org.ie.tk.exception.Commodity;

public class InvalidRating extends Exception{
    public InvalidRating() {
        super("Rating must be an integer between 1 to 10");
    }
}
