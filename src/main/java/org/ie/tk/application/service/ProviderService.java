package org.ie.tk.application.service;

import org.ie.tk.data.Database;
import org.ie.tk.domain.Provider;
import org.ie.tk.exception.Provider.ProviderNotFound;

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
}
