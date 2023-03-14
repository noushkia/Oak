package org.ie.tk.application;

import org.ie.tk.application.service.ServiceLayer;
import org.ie.tk.data.DataLoader;
import org.ie.tk.data.Database;

import java.io.IOException;

public abstract class Handler {
    protected final Database database = new Database();
    protected final ServiceLayer serviceLayer = new ServiceLayer(database);
    protected final String externalServicesUrl = "http://5.253.25.110:5000/";
    public Handler() throws IOException {
        DataLoader dataLoader = new DataLoader(externalServicesUrl);
        dataLoader.getUsers().forEach(database::addUser);
        dataLoader.getProviders().forEach(database::addProvider);
        dataLoader.getCommodities().forEach(database::addCommodity);
        dataLoader.getComments().forEach(database::addComment);
    }
}
