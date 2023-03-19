package com.oak.application.service;

import com.oak.domain.Commodity;
import com.oak.domain.Provider;
import com.oak.domain.User;
import com.oak.exception.Commodity.InvalidRating;
import com.oak.exception.Provider.ProviderNotFound;
import com.oak.exception.User.UserNotFound;
import com.oak.data.Database;
import com.oak.exception.Commodity.CommodityNotFound;

import java.util.List;

public class CommodityService extends Service {

    public CommodityService(Database db) {
        super(db);
    }

    public void addCommodity(Commodity commodity) throws ProviderNotFound {
        Provider provider = db.fetchProvider(commodity.getProviderId());
        db.addCommodity(commodity);
        provider.addCommodity(commodity);
    }


    public List<Commodity> getCommoditiesList() {
        return db.fetchCommodities(c -> true);
    }

    public Commodity getCommodityById(Integer commodityId) throws CommodityNotFound {
        return db.fetchCommodity(commodityId);
    }

    public List<Commodity> getCommoditiesByCategory(String category) {
        return db.fetchCommodities(c -> c.containsCategory(category));
    }

    public List<Commodity> getCommoditiesByPrice(Integer startPrice, Integer endPrice) {
        return db.fetchCommodities(c -> c.isInPriceRange(startPrice, endPrice));
    }

    public void rateCommodity(String username, Integer commodityId, String rating) throws InvalidRating, UserNotFound, CommodityNotFound {
        User user = db.fetchUser(username);
        Commodity commodity = db.fetchCommodity(commodityId);
        commodity.addUserRating(user.getUsername(), rating);
    }

}
