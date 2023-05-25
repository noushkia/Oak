package com.oak.application.service;

import com.oak.data.dao.CommodityDAO;
import com.oak.data.dao.DAOLayer;
import com.oak.data.dao.ProviderDAO;
import com.oak.domain.Provider;
import com.oak.exception.Provider.ProviderNotFound;
import com.oak.data.Database;


public class ProviderService extends Service {
    public ProviderService(Database db, DAOLayer daoLayer) {
        super(db, daoLayer);
    }

    public void addProvider(Provider provider) {
        ProviderDAO providerDAO = daoLayer.getProviderDAO();
        providerDAO.addProvider(provider);
    }

    public Provider getProvider(Integer providerId) throws ProviderNotFound {
        ProviderDAO providerDAO = daoLayer.getProviderDAO();
        return providerDAO.fetchProvider(providerId);
    }
    public Provider getProviderById(Integer providerId) throws ProviderNotFound {
        Provider provider = getProvider(providerId);
        CommodityDAO commodityDAO = daoLayer.getCommodityDAO();
        commodityDAO.fetchProviderCommodities(providerId)
                .forEach(provider::addCommodity);
        return provider;
    }

}
