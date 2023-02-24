package org.ie.tk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.ie.tk.Exception.Commodity.InvalidRating;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

//@JsonIgnoreProperties(ignoreUnknown = true)
public class Commodity {
    private String id;
    private String name;
    private String providerId;
    private Double price;
    private ArrayList<String> categories;
    private Double rating;
    private Integer inStock;

    private final HashMap<String, Double> userRatings = new HashMap<>();

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Commodity other) {
            return Objects.equals(this.id, other.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProviderId() {
        return providerId;
    }

    public Double getPrice() {
        return price;
    }

    public Double getRating() {
        Double sum = rating;
        for (Double userRating : userRatings.values()) {
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

    public void addUserRating(String username, Integer rating) throws InvalidRating {
        //todo: check rating type?
        if (rating < 1 || rating > 10) {
            throw new InvalidRating();
        }
        userRatings.put(username, Double.valueOf(rating));
        //todo
        // check if comm or user is found or not in system
    }

    public Boolean inCategory(String category) {
        return categories.contains(category);
    }
}
