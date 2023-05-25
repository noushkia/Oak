package com.oak.domain;

import com.fasterxml.jackson.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Provider {
    private final Integer id;
    private final String name;
    private final Date registryDate;
    private final String image;

    @JsonProperty("commodities")
    private final HashMap<Integer, Commodity> commodities = new HashMap<>();

    @JsonCreator
    public Provider(@JsonProperty("id") int id,
                    @JsonProperty("name") String name,
                    @JsonProperty("registryDate") @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") Date registryDate,
                    @JsonProperty("image") String image
                    ) {
        this.id = id;
        this.name = name;
        this.registryDate = registryDate;
        this.image = image;
    }

    public Integer getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public void addCommodity(Commodity commodity) {
        commodities.put(commodity.getId(), commodity);
    }
    public Boolean containsName(String name) {
        return this.name.toLowerCase().contains(name.toLowerCase());
    }
    @JsonIgnore
    public List<Commodity> getProvidedCommodities() {
        return new ArrayList<>(commodities.values());
    }
    @JsonIgnore
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
