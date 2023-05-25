package com.oak.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oak.exception.Commodity.CommodityInBuyList;
import com.oak.exception.Commodity.CommodityNotFound;
import com.oak.exception.Commodity.CommodityOutOfStock;

import java.util.Map;

public class BuyList extends CommodityList {
    private Discount discount = null;

    @JsonProperty("final")
    public int calculateFinalCredit() {
        return calculateTotalCredit() - calculateDiscountCredit();
    }

    public int calculateDiscountCredit() {
        return discount != null ? discount.getDiscountPrice(calculateTotalCredit()) : 0;
    }

    @JsonProperty("total")
    public int calculateTotalCredit() {
        return itemsCount.entrySet().stream()
                .mapToInt(entry -> {
                    int itemId = entry.getKey();
                    int itemCount = entry.getValue();
                    Commodity item = items.get(itemId);
                    return item.getPrice() * itemCount;
                })
                .sum();
    }

    public void addItem(String username, Commodity commodity) throws CommodityInBuyList {
        if (items.containsKey(commodity.getId())) {
            throw new CommodityInBuyList(username, commodity.getId());
        }
        items.put(commodity.getId(), commodity);
        itemsCount.put(commodity.getId(), 1);
    }

    public void removeItem(Commodity commodity) throws CommodityNotFound {
        if (!items.containsKey(commodity.getId())) {
            throw new CommodityNotFound(commodity.getId());
        }
        items.remove(commodity.getId());
        itemsCount.remove(commodity.getId());
    }

    public void updateCount(Commodity commodity, Integer quantity) throws CommodityOutOfStock {
        Integer prevCount = itemsCount.get(commodity.getId());
        int newCount = prevCount + quantity;
        commodity.checkInStock(newCount);
        itemsCount.put(commodity.getId(), newCount);
    }

    private void updateStock() {
        for (Map.Entry<Integer, Commodity> entry : items.entrySet()) {
            Integer commodityId = entry.getKey();
            Commodity commodity = entry.getValue();
            commodity.updateStock(-itemsCount.get(commodityId));
        }
    }

    public boolean hasSufficientCredit(Integer credit) {
        return calculateFinalCredit() <= credit;
    }

    public void checkItemsStock() throws CommodityOutOfStock {
        for (Map.Entry<Integer, Commodity> entry : items.entrySet()) {
            Integer commodityId = entry.getKey();
            Commodity commodity = entry.getValue();
            commodity.checkInStock(itemsCount.get(commodityId));
        }
    }

    public Discount getDiscount() {
        return discount;
    }

    public void addDiscount(Discount discount) {
        this.discount = discount;
    }

    public void commitPurchase() {
        updateStock();
    }
}
