package com.oak.application.service;

import com.oak.data.dao.CommodityDAO;
import com.oak.data.dao.DAOLayer;
import com.oak.data.dao.DiscountDAO;
import com.oak.data.dao.UserDAO;
import com.oak.domain.*;
import com.oak.exception.Discount.DiscountNotFound;
import com.oak.exception.Discount.ExpiredDiscount;
import com.oak.exception.User.*;
import com.oak.data.Database;
import com.oak.exception.Commodity.CommodityInBuyList;
import com.oak.exception.Commodity.CommodityNotFound;
import com.oak.exception.Commodity.CommodityOutOfStock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService extends Service {

    public UserService(Database db, DAOLayer daoLayer) {
        super(db, daoLayer);
    }

    public void setUser(User user, Boolean init) throws InvalidUsername {
        user.validate();
        UserDAO userDAO = daoLayer.getUserDAO();
        userDAO.addUser(user, init);
    }

    public void addUser(User user, Boolean init) throws InvalidUsername {
        setUser(user, init);
    }

    public User getUser(String username) throws UserNotFound {
        UserDAO userDAO = daoLayer.getUserDAO();
        return userDAO.fetchUser(username);
    }

    private void prepareUser(User user) {
        UserDAO userDAO = daoLayer.getUserDAO();
        CommodityDAO commodityDAO = daoLayer.getCommodityDAO();
        BuyList buyList = new BuyList();
        buyList.update(userDAO.fetchUserList(user.getUsername(), "BuyList", commodityDAO));
        CommodityList purchasedList = userDAO.fetchUserList(user.getUsername(), "PurchasedList", commodityDAO);

        user.getBuylist().update(buyList);
        user.getPurchasedList().update(purchasedList);

        Discount discount = daoLayer.getDiscountDAO().fetchDiscount(user.getUsername());
        user.getBuylist().addDiscount(discount);
    }
    public User getUserById(String username) throws UserNotFound {
        User user = getUser(username);
        prepareUser(user);
        return user;
    }

    public void checkUsernameDuplication(String username) throws DuplicateUsername {
        try {
          daoLayer.getUserDAO().fetchUser(username);
        } catch (UserNotFound e) {
            return;
        }
        throw new DuplicateUsername(username);
    }

    public void addCredit(String username, Integer credit) throws UserNotFound, NegativeCredit {
        User user = getUser(username);

        if (credit < 0) {
            throw new NegativeCredit();
        }
        UserDAO userDAO = daoLayer.getUserDAO();
        user.addCredit(credit);
        userDAO.updateUserCredit(username, user.getCredit());
    }

    public void addToBuyList(String username, Integer commodityId) throws UserNotFound, CommodityNotFound, CommodityOutOfStock, CommodityInBuyList {
        UserDAO userDAO = daoLayer.getUserDAO();
        User user = getUserById(username);
        CommodityDAO commodityDAO = daoLayer.getCommodityDAO();
        Commodity commodity = commodityDAO.fetchCommodity(commodityId);
        commodity.checkInStock(1);
        user.addToBuyList(commodity);
        userDAO.updateUserList(username, "BuyList", commodityId, 1);
    }

    public void removeFromBuyList(String username, Integer commodityId) throws UserNotFound, CommodityNotFound {
        UserDAO userDAO = daoLayer.getUserDAO();
        User user = getUserById(username);
        CommodityDAO commodityDAO = daoLayer.getCommodityDAO();
        Commodity commodity = commodityDAO.fetchCommodity(commodityId);
        user.removeFromBuyList(commodity);
        userDAO.updateUserList(username, "BuyList", commodityId, 0);
    }

    public void updateBuyListCommodityCount(String username, Integer commodityId, Integer quantity) throws UserNotFound, CommodityNotFound, CommodityOutOfStock {
        UserDAO userDAO = daoLayer.getUserDAO();
        CommodityDAO commodityDAO = daoLayer.getCommodityDAO();

        User user = getUserById(username);
        Commodity commodity = commodityDAO.fetchCommodity(commodityId);
        user.updateBuyListCommodityCount(commodity, quantity);

        HashMap<Integer, Integer> counts = user.getBuylist().getItemsCount();
        userDAO.updateUserList(username, "BuyList", commodityId, counts.get(commodityId));
    }

    public void finalizeBuyList(String username) throws UserNotFound, InsufficientCredit, CommodityOutOfStock {
        UserDAO userDAO = daoLayer.getUserDAO();
        CommodityDAO commodityDAO = daoLayer.getCommodityDAO();
        DiscountDAO discountDAO = daoLayer.getDiscountDAO();

        User user = getUserById(username);
        user.finalizeBuyList();

        userDAO.updateUserCredit(username, user.getCredit());
        userDAO.deleteUserList(username, "BuyList");

        CommodityList purchasedList = user.getPurchasedList();
        HashMap<Integer, Integer> itemsCount = purchasedList.getItemsCount();
        for (Map.Entry<Integer, Integer> entry : itemsCount.entrySet()) {
            Integer commodityId = entry.getKey();
            Integer quantity = entry.getValue();
            userDAO.updateUserList(username, "PurchasedList", commodityId, quantity);
        }

        HashMap<Integer, Commodity> items = user.getBuylist().getItems();
        for (Map.Entry<Integer, Commodity> entry : items.entrySet()) {
            Integer commodityId = entry.getKey();
            Commodity commodity = entry.getValue();
            commodityDAO.updateCommodityInStock(commodityId, commodity.getInStock());
        }

        Discount discount = user.getBuylist().getDiscount();
        if (discount != null) {
            discountDAO.applyDiscount(username, discount.getCode());
        }
    }

    public List<Commodity> getBuyList(String username) throws UserNotFound {
        User user = getUserById(username);
        return user.getBuyListCommodities();
    }

    public void addDiscount(String username, String discountCode) throws UserNotFound, DiscountNotFound, ExpiredDiscount {
        daoLayer.getDiscountDAO().addBuyListDiscount(username, discountCode);
    }

    public void login(String username, String password) throws UserNotFound, InvalidCredentials {
        User user = getUser(username);
        if (!user.authenticate(password)) {
            throw new InvalidCredentials();
        }
    }
}
