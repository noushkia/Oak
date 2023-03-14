package org.ie.tk.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.HashMap;

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
}
