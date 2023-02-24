package org.ie.tk;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String username;
    private String password;
    private String email;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date birthDate;
    private String address;
    private Integer credit;
    private final HashMap<String, Commodity> buyList = new HashMap<>();

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
