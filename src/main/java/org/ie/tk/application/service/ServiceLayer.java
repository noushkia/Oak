package org.ie.tk.application.service;

import org.ie.tk.data.Database;

public class ServiceLayer {

    private ProviderService providerService;
    private CommodityService commodityService;
    private UserService userService;
    public ServiceLayer(Database db) {
        providerService = new ProviderService(db);
        commodityService = new CommodityService(db);
        userService = new UserService(db);
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
}