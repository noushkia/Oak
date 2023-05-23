package com.oak.application.service;

import com.oak.data.Database;
import com.oak.data.dao.DAOLayer;
import com.oak.domain.User;

public class ServiceLayer {
    private final ProviderService providerService;
    private final CommodityService commodityService;
    private final UserService userService;
    private final CommentService commentService;
    private final DiscountService discountService;
    public ServiceLayer(Database db, DAOLayer daoLayer) {
        providerService = new ProviderService(db, daoLayer);
        commodityService = new CommodityService(db, daoLayer);
        userService = new UserService(db, daoLayer);
        commentService = new CommentService(db, daoLayer);
        discountService = new DiscountService(db, daoLayer);
    }

    public ProviderService getProviderService() {
        return providerService;
    }

    public CommodityService getCommodityService() {
        return commodityService;
    }

    public UserService getUserService() {
        return userService;
    }

    public CommentService getCommentService() {
        return commentService;
    }

    public DiscountService getDiscountService() {
        return discountService;
    }
}
