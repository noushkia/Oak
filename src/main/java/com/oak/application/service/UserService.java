package com.oak.application.service;

import com.oak.data.dao.DAOLayer;
import com.oak.data.dao.UserDAO;
import com.oak.domain.Commodity;
import com.oak.domain.User;
import com.oak.exception.Discount.DiscountNotFound;
import com.oak.exception.Discount.ExpiredDiscount;
import com.oak.exception.User.*;
import com.oak.data.Database;
import com.oak.exception.Commodity.CommodityInBuyList;
import com.oak.exception.Commodity.CommodityNotFound;
import com.oak.exception.Commodity.CommodityOutOfStock;

import java.util.List;

public class UserService extends Service {

    public UserService(Database db, DAOLayer daoLayer) {
        super(db, daoLayer);
    }

    public void setUser(User user) throws InvalidUsername {
        user.validate();
        UserDAO userDAO = daoLayer.getUserDAO();
        userDAO.addUser(user);
    }

    public void addUser(User user) throws InvalidUsername {
        setUser(user);
    }

    public User getUserById(String username) throws UserNotFound {
        return db.fetchUser(username);
    }

    public void addCredit(String username, Integer credit) throws UserNotFound, NegativeCredit {
        User user = db.fetchUser(username);
        if (credit < 0) {
            throw new NegativeCredit();
        }
        user.addCredit(credit);
    }

    public void addToBuyList(String username, Integer commodityId) throws UserNotFound, CommodityNotFound, CommodityOutOfStock, CommodityInBuyList {
        User user = db.fetchUser(username);
        Commodity commodity = db.fetchCommodity(commodityId);
        commodity.checkInStock(1);
        user.addToBuyList(commodity);
    }

    public void removeFromBuyList(String username, Integer commodityId) throws UserNotFound, CommodityNotFound {
        User user = db.fetchUser(username);
        Commodity commodity = db.fetchCommodity(commodityId);
        user.removeFromBuyList(commodity);
    }

    public void updateBuyListCommodityCount(String username, Integer commodityId, Integer quantity) throws UserNotFound, CommodityNotFound, CommodityOutOfStock {
        User user = db.fetchUser(username);
        Commodity commodity = db.fetchCommodity(commodityId);
        user.updateBuyListCommodityCount(commodity, quantity);
    }

    public void finalizeBuyList(String username) throws UserNotFound, InsufficientCredit, CommodityOutOfStock {
        User user = db.fetchUser(username);
        user.finalizeBuyList();
    }

    public List<Commodity> getBuyList(String username) throws UserNotFound {
        User user = db.fetchUser(username);
        return user.getBuyListCommodities();
    }

    public List<Commodity> getPurchasedList(String username) throws UserNotFound {
        User user = db.fetchUser(username);
        return user.getPurchasedListCommodities();
    }

    public void addDiscount(String username, String discountCode) throws UserNotFound, DiscountNotFound, ExpiredDiscount {
        daoLayer.getDiscountDAO().addUsedDiscount(username, discountCode);
    }

    public void login(String username, String password) throws UserNotFound, InvalidCredentials {
        User user = db.fetchUser(username);
        if (!user.authenticate(password)) {
            throw new InvalidCredentials();
        }
    }
}
