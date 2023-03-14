package org.ie.tk.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.ie.tk.exception.Commodity.CommodityInBuyList;
import org.ie.tk.exception.Commodity.CommodityNotFound;
import org.ie.tk.exception.User.InvalidUsername;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

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

    public List<Commodity> getBuyList() {
        return (List<Commodity>) buyList.values();
    }

    public void addCredit(Integer credit) {
        this.credit += credit;
    }

    public String getEmail() {
        return email;
    }
}
