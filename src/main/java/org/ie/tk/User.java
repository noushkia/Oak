package org.ie.tk;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.ie.tk.Exception.Commodity.CommodityInBuyList;
import org.ie.tk.Exception.Commodity.CommodityNotFound;
import org.ie.tk.Exception.User.InvalidUsername;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    @JsonProperty("username")
    private String username;
    @JsonProperty("password")
    private String password;
    @JsonProperty("email")
    private String email;
    @JsonProperty("birthDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date birthDate;
    @JsonProperty("address")
    private String address;
    @JsonProperty("credit")
    private Integer credit;
    @JsonIgnore
    private final HashMap<Integer, Commodity> buyList = new HashMap<>();

    public void validate() throws InvalidUsername {
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            throw new InvalidUsername();
        }
    }
    public String getUsername() { return username;}

    public void addToBuyList(Commodity commodity) throws CommodityInBuyList {
        if (buyList.containsKey(commodity.getId())) {
            throw new CommodityInBuyList(this.username, commodity.getId());
        }
        buyList.put(commodity.getId(), commodity);
    }

    public void removeFromBuyList(Commodity commodity) throws CommodityNotFound {
        if (!buyList.containsKey(commodity.getId())){
            throw new CommodityNotFound(commodity.getId());
        }
        buyList.remove(commodity.getId());
    }

    public ArrayList<ObjectNode> getBuyList() {
        ArrayList<ObjectNode> buyListNode = new ArrayList<>();
        for (Commodity commodity : buyList.values()) {
            buyListNode.add(commodity.getObjectNode());
        }
        return buyListNode;
    }

}
