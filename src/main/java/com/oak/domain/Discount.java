package com.oak.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Discount {
    @JsonProperty("discountCode")
    private String discountCode;
    @JsonProperty("discount")
    private Integer discount;


    public String getCode() {
        return discountCode;
    }
}
