package com.oak.data.dao;

public class DAOLayer {
    private final UserDAO userDAO;
    private final ProviderDAO providerDAO;
    private final CommodityDAO commodityDAO;
    private final CommentDAO commentDAO;
    private final DiscountDAO discountDAO;

    // TODO:
    // Create tables
    // CommentVote Table -> commentDAO
    // BuyList Table -> userDAO
    // PurchasedList Table -> userDAO
    // userRatings Table -> userDAO

    public DAOLayer() {
        discountDAO = new DiscountDAO();
        providerDAO = new ProviderDAO();
        commodityDAO = new CommodityDAO();
        commentDAO = new CommentDAO();
        userDAO = new UserDAO();
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public ProviderDAO getProviderDAO() {
        return providerDAO;
    }

    public CommodityDAO getCommodityDAO() {
        return commodityDAO;
    }

    public CommentDAO getCommentDAO() {
        return commentDAO;
    }

    public DiscountDAO getDiscountDAO() {
        return discountDAO;
    }
}
