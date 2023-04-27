package com.oak.application;

import com.oak.application.service.ServiceLayer;
import com.oak.data.DataLoader;
import com.oak.data.Database;
import com.oak.domain.*;
import com.oak.exception.Commodity.CommodityNotFound;
import com.oak.exception.Provider.ProviderNotFound;
import com.oak.exception.User.InvalidUsername;

import java.io.IOException;

public class Server {
    private static Server instance = null;

    protected final Database database = new Database();
    protected final ServiceLayer serviceLayer = new ServiceLayer(database);
    protected final String externalServicesUrl = "http://5.253.25.110:5000/";

    private Server() throws IOException, CommodityNotFound, InvalidUsername, ProviderNotFound {
        DataLoader dataLoader = new DataLoader(externalServicesUrl);
        for (User user: dataLoader.getUsers()) {
            serviceLayer.getUserService().addUser(user);
        }
        for (Provider provider: dataLoader.getProviders()) {
            serviceLayer.getProviderService().addProvider(provider);
        }
        for (Commodity commodity: dataLoader.getCommodities()) {
            serviceLayer.getCommodityService().addCommodity(commodity);
        }
        for (Comment comment: dataLoader.getComments()) {
            serviceLayer.getCommentService().addComment(comment);
        }
//        for (Discount discount: dataLoader.getDiscounts()) {
//            serviceLayer.getDiscountService().addDiscount(discount);
//        }
    }

    public static Server getInstance() {
        if (instance == null) {
            try {
                instance = new Server();
            } catch (InvalidUsername | CommodityNotFound | ProviderNotFound | IOException ignored) {
            }
        }
        return instance;
    }

    public ServiceLayer getServiceLayer() {
        return serviceLayer;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
