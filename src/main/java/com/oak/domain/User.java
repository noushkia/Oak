package com.oak.domain;

import com.fasterxml.jackson.annotation.*;

import com.oak.exception.Discount.ExpiredDiscount;
import com.oak.exception.User.InsufficientCredit;
import com.oak.exception.User.InvalidUsername;
import com.oak.exception.Commodity.CommodityInBuyList;
import com.oak.exception.Commodity.CommodityNotFound;
import com.oak.exception.Commodity.CommodityOutOfStock;

import java.util.*;

public class User {
    private final String username;
    private final String password;
    private final String email;
    private final Date birthDate;
    private final String address;
    private Integer credit;
    @JsonProperty("buyList")
    private final BuyList buyList = new BuyList();
    @JsonProperty("purchasedList")
    private final CommodityList purchasedList = new CommodityList();
    @JsonIgnore
    private final HashSet<String> usedDiscounts = new HashSet<>();

    @JsonCreator
    public User(@JsonProperty("username") String username,
                @JsonProperty("password") String password,
                @JsonProperty("email") String email,
                @JsonProperty("birthDate") @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd") Date birthDate,
                @JsonProperty("address") String address,
                @JsonProperty("credit") Integer credit) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.birthDate = birthDate;
        this.address = address;
        this.credit = credit;
    }

    public void validate() throws InvalidUsername {
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            throw new InvalidUsername();
        }
    }
    public String getUsername() { return username;}

    public String getPassword() { return password;}

    public void addToBuyList(Commodity commodity) throws CommodityInBuyList {
        buyList.addItem(this.username, commodity);
    }

    public void removeFromBuyList(Commodity commodity) throws CommodityNotFound {
        buyList.removeItem(commodity);
    }

    public void updateBuyListCommodityCount(Commodity commodity, Integer quantity) throws CommodityNotFound, CommodityOutOfStock {
        buyList.updateCount(commodity, quantity);
    }

    public void finalizeBuyList() throws InsufficientCredit, CommodityOutOfStock {
        buyList.checkItemsStock();
        if (!buyList.hasSufficientCredit(this.credit)) {
            throw new InsufficientCredit();
        }
        addCredit(-buyList.calculateFinalCredit());
        purchasedList.update(buyList);
        Discount usedDiscount = buyList.getDiscount();
        if (usedDiscount != null) {
            usedDiscounts.add(usedDiscount.getCode());
        }
        buyList.commitPurchase();
    }

    @JsonIgnore
    public List<Commodity> getBuyListCommodities() {
        return new ArrayList<>(buyList.getItems().values());
    }

    @JsonIgnore
    public List<Commodity> getPurchasedListCommodities() {
        return new ArrayList<>(purchasedList.getItems().values());
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

    public void addDiscount(Discount discount) throws ExpiredDiscount {
        if (usedDiscounts.contains(discount.getCode()))
            throw new ExpiredDiscount(discount.getCode());
        buyList.addDiscount(discount);
    }

    @JsonIgnore
    public BuyList getBuylist() {
        return buyList;
    }

    @JsonIgnore
    public CommodityList getPurchasedList() {
        return purchasedList;
    }
}
