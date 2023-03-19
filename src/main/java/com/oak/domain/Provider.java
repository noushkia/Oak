package com.oak.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Provider {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("registryDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date registryDate;
    @JsonIgnore
    private final HashMap<Integer, Commodity> commodities = new HashMap<>();
    public Integer getId() {
        return id;
    }

    public void addCommodity(Commodity commodity) {
        commodities.put(commodity.getId(), commodity);
    }

    public List<Commodity> getProvidedCommodities() {
        return new ArrayList<>(commodities.values());
    }

    public Double getRating() {
        Double sum = 0.0;
        for (Commodity commodity : commodities.values()) {
            sum += commodity.getRating();
        }
        return sum / commodities.size();
    }

    public String getName() {
        return name;
    }

    public Date getRegistryDate() {
        return registryDate;
    }
}
