package com.oak.application.service;

import com.oak.domain.Provider;
import com.oak.exception.Provider.ProviderNotFound;
import com.oak.data.Database;

import java.util.List;

public class ProviderService extends Service {
    public ProviderService(Database db) {
        super(db);
    }

    public void addProvider(Provider provider) {
            db.addProvider(provider);
    }

    public Provider getProviderById(Integer providerId) throws ProviderNotFound {
        return db.fetchProvider(providerId);
    }

    public List<Provider> getProvidersByName(String name) {
        return db.fetchProviders(p -> p.containsName(name));
    }
}
