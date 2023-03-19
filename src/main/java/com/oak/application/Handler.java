package com.oak.application;

import com.oak.application.service.ServiceLayer;
import com.oak.domain.Comment;
import com.oak.domain.Commodity;
import com.oak.domain.Provider;
import com.oak.domain.User;
import com.oak.exception.Provider.ProviderNotFound;
import com.oak.exception.User.InvalidUsername;
import com.oak.data.DataLoader;
import com.oak.data.Database;
import com.oak.exception.Commodity.CommodityNotFound;

import java.io.IOException;

public abstract class Handler {
    protected final Database database = new Database();
    protected final ServiceLayer serviceLayer = new ServiceLayer(database);
    protected final String externalServicesUrl = "http://5.253.25.110:5000/";
    public Handler() throws IOException, CommodityNotFound, InvalidUsername, ProviderNotFound {
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
    }
}
