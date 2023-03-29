package com.oak.exception.Discount;

public class DiscountNotFound extends Exception {
    public DiscountNotFound(String code) {
        super("Discount with code" + code + " not found!");
    }
}
