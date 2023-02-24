package org.ie.tk;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ie.tk.Exception.Commodity.CommodityInBuyList;
import org.ie.tk.Exception.Commodity.CommodityNotFound;
import org.ie.tk.Exception.User.InvalidUsername;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class User {
    private String username;
    private String password;
    private String email;

    private Date birthDate;
    private String address;
    private Integer credit;
    private final HashMap<String, Commodity> buyList;

    @JsonCreator
    public User(@JsonProperty("username") String username,
                @JsonProperty("password") String password,
                @JsonProperty("email") String email,
                @JsonProperty("birthDate") String birthDate,
                @JsonProperty("address") String address,
                @JsonProperty("credit") Integer credit) throws InvalidUsername, ParseException {
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            throw new InvalidUsername();
        }
        this.username = username;
        this.password = password;
        this.email = email;
        this.birthDate = new SimpleDateFormat("yyyy-MM-dd").parse(birthDate);
        this.address = address;
        this.credit = credit;
        this.buyList = new HashMap<>();
    }

    public void addToBuyList(Commodity commodity) throws CommodityInBuyList {
        if (buyList.containsKey(commodity.getId())) {
            throw new CommodityInBuyList(this.username, commodity.getId());
        }
        buyList.put(commodity.getId(), commodity);
        //TODO
        // Decrease me there in system
        // Check found and inStock in system
    }

    public void removeFromBuyList(Commodity commodity) throws CommodityNotFound {
        if (!buyList.containsKey(commodity.getId())){
            throw new CommodityNotFound(commodity.getId());
        }
        buyList.remove(commodity.getId());
        //TODO
        // Increase inStock
    }

    public ArrayNode getBuyList() {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode buyListNode = objectMapper.createArrayNode();

        for (Commodity commodity : buyList.values()) {
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
