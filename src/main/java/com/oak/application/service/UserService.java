package com.oak.application.service;

import com.oak.domain.Commodity;
import com.oak.domain.User;
import com.oak.exception.User.InsufficientCredit;
import com.oak.exception.User.InvalidUsername;
import com.oak.exception.User.UserNotFound;
import com.oak.data.Database;
import com.oak.exception.Commodity.CommodityInBuyList;
import com.oak.exception.Commodity.CommodityNotFound;
import com.oak.exception.Commodity.CommodityOutOfStock;

import java.util.List;

public class UserService extends Service {

    public UserService(Database db) {
        super(db);
    }

    public void setUser(User user) throws InvalidUsername {
        user.validate();
        db.addUser(user);
    }

    public void addUser(User user) throws InvalidUsername {
        setUser(user);
    }

    public User getUserById(String username) throws UserNotFound {
        return db.fetchUser(username);
    }

    public void addCredit(String username, Integer credit) throws UserNotFound {
        User user = db.fetchUser(username);
        user.addCredit(credit);
    }

    public void addToBuyList(String username, Integer commodityId) throws UserNotFound, CommodityNotFound, CommodityOutOfStock, CommodityInBuyList {
        User user = db.fetchUser(username);
        Commodity commodity = db.fetchCommodity(commodityId);
        commodity.checkInStock();
        user.addToBuyList(commodity);
    }

    public void removeFromBuyList(String username, Integer commodityId) throws UserNotFound, CommodityNotFound {
        User user = db.fetchUser(username);
        Commodity commodity = db.fetchCommodity(commodityId);
        user.removeFromBuyList(commodity);
    }

    public void finalizeBuyList(String username) throws UserNotFound, InsufficientCredit, CommodityOutOfStock {
        User user = db.fetchUser(username);
        user.finalizeBuyList();
    }

    public List<Commodity> getBuyList(String username) throws UserNotFound {
        User user = db.fetchUser(username);
        return user.getBuyList();
    }

    public List<Commodity> getPurchasedList(String username) throws UserNotFound {
        User user = db.fetchUser(username);
        return user.getPurchasedList();
    }

}
