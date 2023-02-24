package org.ie.tk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.ie.tk.Exception.Commodity.InvalidRating;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Commodity {
    private String id;
    private String name;
    private String providerId;
    private Integer price;
    private ArrayList<String> categories;
    private Double rating;
    private Integer inStock;

    private final HashMap<String, Integer> userRatings = new HashMap<>();

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProviderId() {
        return providerId;
    }

    public Integer getPrice() {
        return price;
    }

    public Double getRating() {
        Double sum = rating;
        for (Integer userRating : userRatings.values()) {
            sum += userRating;
        }
        return sum / (userRatings.size() + 1);
    }

    public ArrayNode getCategories() {
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode categoriesNode = objectMapper.createArrayNode();
        for (String category: categories) {
            categoriesNode.add(category);
        }
        return categoriesNode;
    }

    public void addUserRating(String username, String rating) throws InvalidRating {
        try {
            int ratingValue = Integer.parseInt(rating);
            if (ratingValue < 1 || ratingValue > 10) {
                throw new InvalidRating();
            }
            userRatings.put(username, ratingValue);
        } catch (NumberFormatException e) {
            throw  new InvalidRating();
        }
        //todo
        // check if comm or user is found or not in system
    }

    public Boolean isInCategory(String category) {
        return categories.contains(category);
    }
}
