package com.oak.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
public class Discount {
    @JsonProperty("discountCode")
    private String discountCode;
    @JsonProperty("discount")
    private Integer discount;

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
