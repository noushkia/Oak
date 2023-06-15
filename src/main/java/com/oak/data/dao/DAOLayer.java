package com.oak.data.dao;

import java.sql.SQLException;

public class DAOLayer {
    private UserDAO userDAO = null;
    private ProviderDAO providerDAO = null;
    private CommodityDAO commodityDAO = null;
    private CommentDAO commentDAO = null;
    private DiscountDAO discountDAO = null;

    public DAOLayer() {
        try {
            discountDAO = new DiscountDAO();
            providerDAO = new ProviderDAO();
            commodityDAO = new CommodityDAO();
            commentDAO = new CommentDAO();
            userDAO = new UserDAO();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
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
