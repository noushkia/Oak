package org.ie.tk;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ie.tk.Exception.Commodity.CommodityOutOfStock;
import org.ie.tk.Exception.Commodity.InvalidRating;
import org.ie.tk.utils.CategoriesDeserializer;
import org.ie.tk.utils.CategoriesSerializer;

import java.util.ArrayList;
import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Commodity {
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("providerId")
    private String providerId;
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

    public String getId() {
        return id;
    }

    public String getProviderId() {
        return providerId;
    }

    public Double getRating() {
        Double sum = rating;
        for (Integer userRating : userRatings.values()) {
            sum += userRating;
        }
        return sum / (userRatings.size() + 1);
    }

    public ObjectNode getObjectNode() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode commodityNode = objectMapper.createObjectNode();
        commodityNode.put("id", id);
        commodityNode.put("name", name);
        commodityNode.put("providerId", providerId);
        commodityNode.put("price", price);
        commodityNode.set("categories", objectMapper.valueToTree(categories));
        commodityNode.put("rating", getRating());
        return commodityNode;
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

    public Boolean isInCategory(String category) {
        return categories.contains(category);
    }

    public void updateStock(Integer amount) {
        inStock += amount;
    }

    public void validate() throws CommodityOutOfStock {
        if (inStock == 0) {
            throw new CommodityOutOfStock(id);
        }
    }

    public HashMap<String, Integer> getUserRatings() {
        return userRatings;
    }

    public int getInStock() {
        return inStock;
    }
}
