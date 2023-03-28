package com.oak.domain;

import com.oak.exception.Commodity.CommodityInBuyList;
import com.oak.exception.Commodity.CommodityNotFound;
import com.oak.exception.Commodity.CommodityOutOfStock;

import java.util.HashMap;

public class BuyList {
    private final HashMap<Integer, Commodity> items = new HashMap<>();
    private Discount discount = null;

    public int calculateFinalCredit() {
        return calculateTotalCredit() - calculateDiscountCredit();
    }

    public int calculateDiscountCredit() {
        return discount != null ? discount.getDiscountPrice(calculateTotalCredit()) : 0;
    }

    public int calculateTotalCredit() {
        return items.values().stream().mapToInt(Commodity::getPrice).sum();
    }

    public void addItem(String username, Commodity commodity) throws CommodityInBuyList {
        if (items.containsKey(commodity.getId())) {
            throw new CommodityInBuyList(username, commodity.getId());
        }
        items.put(commodity.getId(), commodity);
    }

    public void removeItem(Commodity commodity) throws CommodityNotFound {
        if (!items.containsKey(commodity.getId())) {
            throw new CommodityNotFound(commodity.getId());
        }
        items.remove(commodity.getId());
    }

    public void updateStock() {
        for (Commodity commodity : items.values()) {
            commodity.updateStock(-1);
        }
        items.clear();
    }

    public HashMap<Integer, Commodity> getItems() {
        return items;
    }

    public boolean hasSufficientCredit(Integer credit) {
        return calculateFinalCredit() <= credit;
    }

    public void checkItemsStock() throws CommodityOutOfStock {
        for (Commodity commodity : items.values()) {
            commodity.checkInStock();
        }
    }

    public Discount getDiscount() {
        return discount;
    }

    public void useDiscount() {
        discount = null;
    }

    public void addDiscount(Discount discount) {
        this.discount = discount;
    }

    public void commitPurchase() {
        updateStock();
        useDiscount();
    }
}
