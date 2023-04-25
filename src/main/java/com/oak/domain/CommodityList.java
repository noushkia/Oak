package com.oak.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

public class CommodityList {
    @JsonProperty("items")
    protected final HashMap<Integer, Commodity> items = new HashMap<>();
    @JsonProperty("itemsCount")
    protected final HashMap<Integer, Integer> itemsCount = new HashMap<>();

    public HashMap<Integer, Commodity> getItems() {
        return items;
    }

    public void update(CommodityList other) {
        other.itemsCount.forEach((id, count) -> {
            if (this.items.containsKey(id)) {
                Integer thisCount = this.itemsCount.get(id);
                this.itemsCount.put(id, count + thisCount);
            }
            else {
                Commodity commodity = other.items.get(id);
                this.items.put(id, commodity);
                this.itemsCount.put(id, count);
            }
        });
    }
}
