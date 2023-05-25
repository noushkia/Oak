package com.oak.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Discount {
    private final String discountCode;
    private final Integer discount;

    @JsonCreator
    public Discount(@JsonProperty("discountCode") String discountCode,
                    @JsonProperty("discount") Integer discount) {
        this.discountCode = discountCode;
        this.discount = discount;
    }

    public Integer getDiscountPrice(Integer totalPrice) {
        return (int) (totalPrice * discount / 100.0);
    }

    public String getCode() {
        return discountCode;
    }

    public Integer getDiscount() {
        return discount;
    }
}
