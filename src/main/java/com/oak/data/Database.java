package com.oak.data;

import com.oak.domain.*;
import com.oak.exception.Comment.CommentNotFound;
import com.oak.exception.User.UserNotFound;
import com.oak.exception.Commodity.CommodityNotFound;

import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    public Commodity fetchCommodity(Integer commodityId) throws CommodityNotFound {
        if (!commodities.containsKey(commodityId)) {
            throw new CommodityNotFound(commodityId);
        }
        return commodities.get(commodityId);
    }

    public Comment fetchComment(Integer commentId) throws CommentNotFound {
        if (!comments.containsKey(commentId)) {
            throw new CommentNotFound(commentId);
        }
        return comments.get(commentId);
    }

    public List<Commodity> fetchCommodities(Predicate<Commodity> predicate) {
        return commodities.values()
                .stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public List<Provider> fetchProviders(Predicate<Provider> predicate) {
        return providers.values()
                .stream()
                .filter(predicate)
                .collect(Collectors.toList());
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
