package com.oak.application.service;

import com.oak.domain.Commodity;
import com.oak.domain.Provider;
import com.oak.domain.User;
import com.oak.exception.Commodity.InvalidRating;
import com.oak.exception.Provider.ProviderNotFound;
import com.oak.exception.User.UserNotFound;
import com.oak.data.Database;
import com.oak.exception.Commodity.CommodityNotFound;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class CommodityService extends Service {
    private Predicate<Commodity> query = c -> true;
    private Comparator<Commodity> comparator = null;
    public CommodityService(Database db) {
        super(db);
    }

    public void setQuery(String method, String input) {
        if (method.contains("category")) {
            query = c -> c.containsCategory(input);
        }
        else if(method.contains("name")) {
            query = c -> c.containsName(input);
        }
    }

    public void setComparator(String method) {
        if (method.contains("rating")) {
            comparator = Comparator.comparing(Commodity::getRating);
        }
        else if (method.contains("price")) {
            comparator = Comparator.comparing(Commodity::getPrice);
        }
    }

    public void reset() {
        query = c -> true;
        comparator = null;
    }

    public void addCommodity(Commodity commodity) throws ProviderNotFound {
        Provider provider = db.fetchProvider(commodity.getProviderId());
        db.addCommodity(commodity);
        provider.addCommodity(commodity);
    }


    public List<Commodity> getCommoditiesList() {
        List<Commodity> commodities = db.fetchCommodities(query);
        if (comparator != null) {
            commodities.sort(comparator);
        }
        return commodities;
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
