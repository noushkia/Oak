package org.ie.tk.data;

import org.ie.tk.domain.Comment;
import org.ie.tk.exception.Commodity.CommodityNotFound;
import org.ie.tk.exception.Provider.ProviderNotFound;
import org.ie.tk.exception.User.UserNotFound;
import org.ie.tk.domain.Commodity;
import org.ie.tk.domain.Provider;
import org.ie.tk.domain.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class Database {
    private final HashMap<Integer, Commodity> commodities;
    private final HashMap<Integer, Provider> providers;
    private final HashMap<String, User> users;
    private final HashMap<Integer, Comment> comments;

    public Database() {
        commodities = new HashMap<>();
        providers = new HashMap<>();
        users = new HashMap<>();
        comments = new HashMap<>();
    }

    public User fetchUser(String username) throws UserNotFound {
        if (!users.containsKey(username)) {
            throw new UserNotFound(username);
        }
        return users.get(username);
    }

    public Provider fetchProvider(Integer providerId) throws ProviderNotFound {
        if (!providers.containsKey(providerId)) {
            throw new ProviderNotFound(providerId);
        }
        return providers.get(providerId);
    }

    public Commodity fetchCommodity(Integer commodityId) throws CommodityNotFound {
        if (!commodities.containsKey(commodityId)) {
            throw new CommodityNotFound(commodityId);
        }
        return commodities.get(commodityId);
    }

    public List<Commodity> fetchCommodities(Predicate<Commodity> predicate) {
        List<Commodity> filteredCommodities = new ArrayList<>();

        for (Commodity commodity : commodities.values()) {
            if (predicate.test(commodity)) {
                filteredCommodities.add(commodity);
            }
        }

        return filteredCommodities;
    }

    public void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    public void addProvider(Provider provider) {
        providers.put(provider.getId(), provider);
    }

    public void addCommodity(Commodity commodity) {
        commodities.put(commodity.getId(), commodity);
    }
    public void addComment(Comment comment) {
        comment.setId();
        comments.put(comment.getId(), comment);
    }
}
