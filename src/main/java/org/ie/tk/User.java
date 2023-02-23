package org.ie.tk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ie.tk.Exception.CommodityInBuyList;
import org.ie.tk.Exception.CommodityNotFound;

import java.util.ArrayList;

public class User {
    private String username;
    private String email;
    private String password;
    private String birthDate;
    private String address;
    private Integer credit;
    private final ArrayList<Commodity> buyList = new ArrayList<>();

    public void addToBuyList(Commodity commodity) throws CommodityInBuyList {
        if (buyList.contains(commodity)) {
            throw new CommodityInBuyList(this.username, commodity.getId());
        }
        buyList.add(commodity);
        //TODO
        // Decrease me there in system
        // Check found and inStock in system
    }

    public void removeFromBuyList(Commodity commodity) throws CommodityNotFound {
        if (!buyList.contains(commodity)){
            throw new CommodityNotFound(commodity.getId());
        }
        buyList.remove(commodity);
        //TODO
        // Increase inStock
    }

    public ArrayNode getBuyList() {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode buyListNode = objectMapper.createArrayNode();

        for (Commodity commodity : buyList) {
            ObjectNode commodityNode = objectMapper.createObjectNode();
            commodityNode.put("id", commodity.getId());
            commodityNode.put("name", commodity.getName());
            commodityNode.put("providerId", commodity.getProviderId());
            commodityNode.put("price", commodity.getPrice());
            commodityNode.set("categories", commodity.getCategories());
            commodityNode.put("rating", commodity.getRating());
            buyListNode.add(commodityNode);
        }

        return buyListNode;
    }

}
