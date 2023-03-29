package com.oak.application.service;

import com.oak.data.Database;
import com.oak.domain.User;

public class ServiceLayer {
    private User currentUser = null;
    private final ProviderService providerService;
    private final CommodityService commodityService;
    private final UserService userService;
    private final CommentService commentService;
    private final DiscountService discountService;
    public ServiceLayer(Database db) {
        providerService = new ProviderService(db);
        commodityService = new CommodityService(db);
        userService = new UserService(db);
        commentService = new CommentService(db);
        discountService = new DiscountService(db);
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

    public User getCurrentUser() {
        return currentUser;
    }

    public void setUser(User user) {
        currentUser = user;
    }
}
