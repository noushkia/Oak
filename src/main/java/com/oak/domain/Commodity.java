package com.oak.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.oak.exception.Commodity.InvalidRating;
import com.oak.exception.Commodity.CommodityOutOfStock;

import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Commodity {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("providerId")
    private Integer providerId;
    @JsonProperty("price")
    private Integer price;
    @JsonProperty("categories")
    private ArrayList<String> categories;
    @JsonProperty("rating")
    private Double rating;
    @JsonProperty("inStock")
    private Integer inStock;

    @JsonProperty("image")
    private String image;

    @JsonIgnore
    private final HashMap<String, Integer> userRatings = new HashMap<>();

    @JsonProperty("comments")
    private final HashMap<Integer, Comment> userComments = new HashMap<>();

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getProviderId() {
        return providerId;
    }
    @JsonProperty("rating")
    public Double getRating() {
        Double sum = rating;
        for (Integer userRating : userRatings.values()) {
            sum += userRating;
        }
        return sum / (userRatings.size() + 1);
    }

    @JsonProperty("ratings")
    public Integer getRatings() {
        return userRatings.size() + 1;
    }

    public void addUserRating(String username, String rating) throws InvalidRating {
        try {
            int ratingValue = Integer.parseInt(rating);
            if (ratingValue < 1 || ratingValue > 10) {
                throw new InvalidRating();
            }
            userRatings.put(username, ratingValue);
        } catch (NumberFormatException e) {
            throw new InvalidRating();
        }
    }

    public Boolean containsCategory(String category) {
        return categories.contains(category);
    }

    public Boolean isInSimilarCategory(Commodity commodity) {
        Set<String> currentCategories = new HashSet<>(categories);
        Set<String> otherCategories = new HashSet<>(commodity.getCategories());

        return !Collections.disjoint(currentCategories, otherCategories);
    }


    public Boolean isInPriceRange(Integer startPrice, Integer endPrice) {
        return (price >= startPrice) & (price <= endPrice);
    }

    public Boolean containsName(String name) {
        return this.name.toLowerCase().contains(name.toLowerCase());
    }

    public void updateStock(Integer amount) {
        inStock += amount;
    }

    public void checkInStock(Integer count) throws CommodityOutOfStock {
        if (inStock < count) {
            throw new CommodityOutOfStock(id);
        }
    }

    public Boolean isAvailable() {
        try {
            checkInStock(1);
        } catch (CommodityOutOfStock e) {
            return false;
        }
        return true;
    }

    public HashMap<String, Integer> getUserRatings() {
        return userRatings;
    }

    public Integer getInStock() {
        return inStock;
    }

    public void addComment(Comment comment) {
        userComments.put(comment.getId(), comment);
    }

    public Integer getPrice() {
        return price;
    }

    public List<String> getCategories() {
        return categories;
    }

    public List<Comment> getUserComments() {
        return new ArrayList<>(userComments.values());
    }
}
