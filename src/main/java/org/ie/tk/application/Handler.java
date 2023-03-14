package org.ie.tk.application;

import org.ie.tk.application.service.ServiceLayer;
import org.ie.tk.data.DataLoader;
import org.ie.tk.data.Database;
import org.ie.tk.domain.Comment;
import org.ie.tk.domain.Commodity;
import org.ie.tk.domain.Provider;
import org.ie.tk.domain.User;
import org.ie.tk.exception.Commodity.CommodityNotFound;
import org.ie.tk.exception.Provider.ProviderNotFound;
import org.ie.tk.exception.User.InvalidUsername;

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
            serviceLayer.getCommodityService().addComment(comment);
        }
    }
}
