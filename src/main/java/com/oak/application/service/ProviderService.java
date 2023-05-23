package com.oak.application.service;

import com.oak.data.dao.CommodityDAO;
import com.oak.data.dao.DAOLayer;
import com.oak.data.dao.ProviderDAO;
import com.oak.domain.Commodity;
import com.oak.domain.Provider;
import com.oak.exception.Provider.ProviderNotFound;
import com.oak.data.Database;

import java.util.List;

public class ProviderService extends Service {
    public ProviderService(Database db, DAOLayer daoLayer) {
        super(db, daoLayer);
    }

    public void addProvider(Provider provider) {
        ProviderDAO providerDAO = daoLayer.getProviderDAO();
        providerDAO.addProvider(provider);
    }

    public Provider getProviderById(Integer providerId) throws ProviderNotFound {
        ProviderDAO providerDAO = daoLayer.getProviderDAO();
        Provider provider = providerDAO.fetchProvider(providerId);
        CommodityDAO commodityDAO = daoLayer.getCommodityDAO();
        // TODO: fetch commodities with providerId
        commodityDAO.fetchCommodities();
        return provider;
    }

    public List<Provider> getProvidersByName(String name) {
        return db.fetchProviders(p -> p.containsName(name));
    }
}
