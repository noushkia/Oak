package com.oak.application.service;

import com.oak.data.dao.CommodityDAO;
import com.oak.data.dao.DAOLayer;
import com.oak.data.dao.UserDAO;
import com.oak.domain.*;
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

    public User getUser(String username) throws UserNotFound {
        UserDAO userDAO = daoLayer.getUserDAO();
        return userDAO.fetchUser(username);
    }

    public User getUserById(String username) throws UserNotFound {
        User user = getUser(username);

        UserDAO userDAO = daoLayer.getUserDAO();
        CommodityDAO commodityDAO = daoLayer.getCommodityDAO();
        BuyList buyList = (BuyList) userDAO.fetchUserList(username, "BuyList", commodityDAO);
        CommodityList purchasedList = userDAO.fetchUserList(username, "PurchasedList", commodityDAO);

        user.getBuylist().update(buyList);
        user.getPurchasedList().update(purchasedList);

        return user;
    }

    public void addCredit(String username, Integer credit) throws UserNotFound, NegativeCredit {
        User user = getUser(username);

        if (credit < 0) {
            throw new NegativeCredit();
        }
        UserDAO userDAO = daoLayer.getUserDAO();
        user.addCredit(credit);
        userDAO.updateUserCredit(username, credit);
    }

    public void addToBuyList(String username, Integer commodityId) throws UserNotFound, CommodityNotFound, CommodityOutOfStock, CommodityInBuyList {
        UserDAO userDAO = daoLayer.getUserDAO();
        User user = getUserById(username);
        CommodityDAO commodityDAO = daoLayer.getCommodityDAO();
        Commodity commodity = commodityDAO.fetchCommodity(commodityId);
        commodity.checkInStock(1);
        user.addToBuyList(commodity);
        userDAO.updateUserBuyList(username, commodityId, 1);
    }

    public void removeFromBuyList(String username, Integer commodityId) throws UserNotFound, CommodityNotFound {
        UserDAO userDAO = daoLayer.getUserDAO();
        User user = getUserById(username);
        CommodityDAO commodityDAO = daoLayer.getCommodityDAO();
        Commodity commodity = commodityDAO.fetchCommodity(commodityId);
        user.removeFromBuyList(commodity);
        userDAO.updateUserBuyList(username, commodityId, 0);
    }

    public void updateBuyListCommodityCount(String username, Integer commodityId, Integer quantity) throws UserNotFound, CommodityNotFound, CommodityOutOfStock {
        UserDAO userDAO = daoLayer.getUserDAO();
        User user = getUserById(username);
        CommodityDAO commodityDAO = daoLayer.getCommodityDAO();
        Commodity commodity = commodityDAO.fetchCommodity(commodityId);
        user.updateBuyListCommodityCount(commodity, quantity);
        userDAO.updateUserBuyList(username, commodityId, quantity);
    }

    public void finalizeBuyList(String username) throws UserNotFound, InsufficientCredit, CommodityOutOfStock {
        UserDAO userDAO = daoLayer.getUserDAO();
        User user = getUserById(username);
        BuyList buyList = user.getBuylist();
        user.finalizeBuyList();

        userDAO.updateUserCredit(username, -buyList.calculateFinalCredit());
        userDAO.finalizeBuyList(username);
        Discount usedDiscount = buyList.getDiscount();
        if (usedDiscount != null) {
            try {
                addDiscount(username, usedDiscount.getCode());
            } catch (DiscountNotFound | ExpiredDiscount ignored) {}
        }
        // todo: store discount for buy lists?
    }

    public List<Commodity> getBuyList(String username) throws UserNotFound {
        User user = getUserById(username);
        return user.getBuyListCommodities();
    }

    public List<Commodity> getPurchasedList(String username) throws UserNotFound {
        User user = getUserById(username);
        return user.getPurchasedListCommodities();
    }

    public void addDiscount(String username, String discountCode) throws UserNotFound, DiscountNotFound, ExpiredDiscount {
        daoLayer.getDiscountDAO().addUsedDiscount(username, discountCode);
    }

    public void login(String username, String password) throws UserNotFound, InvalidCredentials {
        User user = getUser(username);
        if (!user.authenticate(password)) {
            throw new InvalidCredentials();
        }
    }
}
