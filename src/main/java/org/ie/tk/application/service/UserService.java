package org.ie.tk.application.service;

import org.ie.tk.data.Database;
import org.ie.tk.exception.Commodity.CommodityInBuyList;
import org.ie.tk.exception.Commodity.CommodityNotFound;
import org.ie.tk.exception.Commodity.CommodityOutOfStock;
import org.ie.tk.exception.User.InvalidUsername;
import org.ie.tk.domain.Commodity;
import org.ie.tk.domain.User;
import org.ie.tk.exception.User.UserNotFound;

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
        commodity.validate();
        user.addToBuyList(commodity);
    }

    public void removeFromBuyList(String username, Integer commodityId) throws UserNotFound, CommodityNotFound {
        User user = db.fetchUser(username);
        Commodity commodity = db.fetchCommodity(commodityId);
        user.removeFromBuyList(commodity);
    }

    public List<Commodity> getBuyList(String username) throws UserNotFound {
        User user = db.fetchUser(username);
        return user.getBuyList();
    }

}
