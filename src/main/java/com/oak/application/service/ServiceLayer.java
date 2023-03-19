package com.oak.application.service;

import com.oak.data.Database;

public class ServiceLayer {

    private final ProviderService providerService;
    private final CommodityService commodityService;
    private final UserService userService;
    private final CommentService commentService;
    public ServiceLayer(Database db) {
        providerService = new ProviderService(db);
        commodityService = new CommodityService(db);
        userService = new UserService(db);
        commentService = new CommentService(db);
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
}
