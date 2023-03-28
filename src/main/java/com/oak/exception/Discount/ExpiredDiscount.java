package com.oak.exception.Discount;

public class ExpiredDiscount extends Exception{
    public ExpiredDiscount(String code) {
        super("Discount with code " + code + " is expired!");
    }
}
