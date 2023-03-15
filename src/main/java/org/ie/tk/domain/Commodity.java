package org.ie.tk.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.ie.tk.exception.Commodity.CommodityOutOfStock;
import org.ie.tk.exception.Commodity.InvalidRating;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    @JsonIgnore
    private final HashMap<String, Integer> userRatings = new HashMap<>();

    @JsonIgnore
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

    public Double getRating() {
        Double sum = rating;
        for (Integer userRating : userRatings.values()) {
            sum += userRating;
        }
        return sum / (userRatings.size() + 1);
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

    public void updateStock(Integer amount) {
        inStock += amount;
    }

    public void checkInStock() throws CommodityOutOfStock {
        if (inStock == 0) {
            throw new CommodityOutOfStock(id);
        }
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
