package org.ie.tk.domain;

import org.ie.tk.exception.Commodity.CommodityInBuyList;
import org.ie.tk.exception.Commodity.CommodityNotFound;
import org.ie.tk.exception.Commodity.CommodityOutOfStock;

import java.util.HashMap;

public class BuyList {
    private final HashMap<Integer, Commodity> items = new HashMap<>();
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
        return calculateTotalCredit() <= credit;
    }

    public void checkItemsStock() throws CommodityOutOfStock {
        for (Commodity commodity: items.values()) {
            commodity.checkInStock();
        }
    }
}
