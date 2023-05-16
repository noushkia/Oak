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
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CommodityService extends Service {
    private Predicate<Commodity> query = c -> true;
    private Comparator<Commodity> comparator = null;

    public CommodityService(Database db) {
        super(db);
    }

    public void setQuery(String method, String input) {
        if (method.contains("category")) {
            query = query.and(c -> c.containsCategory(input));
        } else if (method.contains("name")) {
            final String lowercaseInput = input.toLowerCase();
            query = query.and(c -> c.containsName(lowercaseInput));
        }
    }

    public void setQuery(List<Provider> input) {
        Set<Integer> providerIds = input.stream()
                .map(Provider::getId)
                .collect(Collectors.toSet());
        query = query.and(c -> providerIds.contains(c.getProviderId()));
    }

    public void setQuery(String method) {
        if (method.contains("onlyAvailableCommodities")) {
            query = query.and(Commodity::isAvailable);
        }
    }

    public void setComparator(String method) {
        if (method.contains("rating")) {
            comparator = Comparator.comparing(Commodity::getRating).reversed();
        } else if (method.contains("price")) {
            comparator = Comparator.comparing(Commodity::getPrice).reversed();
        } else if (method.contains("name")) {
            comparator = Comparator.comparing(Commodity::getName);
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

    public List<Commodity> getSuggestedCommodities(Integer commodityId) throws CommodityNotFound {
        Commodity inputCommodity = db.fetchCommodity(commodityId);
        List<Commodity> allCommodities = db.fetchCommodities(c -> true);
        Comparator<Commodity> scoringFunc = Comparator.comparingDouble(c -> (c.isInSimilarCategory(inputCommodity) ? 11.0 : 0.0) + c.getRating());
        return allCommodities.stream()
                .filter(c -> !c.getId().equals(commodityId)) // Skip the input Commodity object
                .sorted(scoringFunc.reversed().thenComparingInt(c -> (int) (Math.random() * 1000)))
                .limit(5)
                .collect(Collectors.toList());
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
