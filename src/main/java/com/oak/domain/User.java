package com.oak.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.oak.exception.User.InsufficientCredit;
import com.oak.exception.User.InvalidUsername;
import com.oak.exception.Commodity.CommodityInBuyList;
import com.oak.exception.Commodity.CommodityNotFound;
import com.oak.exception.Commodity.CommodityOutOfStock;

import java.util.*;

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
    private final BuyList buyList = new BuyList();

    @JsonIgnore
    private final HashMap<Integer, Commodity> purchasedList = new HashMap<>();

    public void validate() throws InvalidUsername {
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            throw new InvalidUsername();
        }
    }
    public String getUsername() { return username;}

    public void addToBuyList(Commodity commodity) throws CommodityInBuyList {
        buyList.addItem(this.username, commodity);
    }

    public void removeFromBuyList(Commodity commodity) throws CommodityNotFound {
        buyList.removeItem(commodity);
    }

    public void finalizeBuyList() throws InsufficientCredit, CommodityOutOfStock {
        buyList.checkItemsStock();
        if (!buyList.hasSufficientCredit(this.credit)) {
            throw new InsufficientCredit();
        }
        addCredit(-buyList.calculateTotalCredit());
        purchasedList.putAll(buyList.getItems());
        buyList.updateStock();
    }

    public List<Commodity> getBuyList() {
        return new ArrayList<>(buyList.getItems().values());
    }

    public List<Commodity> getPurchasedList() {
        return new ArrayList<>(purchasedList.values());
    }

    public void addCredit(Integer credit) {
        this.credit += credit;
    }

    public String getEmail() {
        return email;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public String getAddress() {
        return address;
    }

    public Integer getCredit() {
        return credit;
    }

    public boolean authenticate(String password) {
        return Objects.equals(this.password, password);
    }
}
