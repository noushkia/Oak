package org.ie.tk;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jdk.jshell.spi.ExecutionControl;
import org.ie.tk.Exception.Commodity.CommodityNotFound;
import org.ie.tk.Exception.Provider.ProviderNotFound;
import org.ie.tk.Exception.User.UserNotFound;

import java.util.ArrayList;
import java.util.HashMap;

public class CommodityProvisionSystem {

    private final HashMap<Integer, Commodity> commodities;
    private final HashMap<Integer, Provider> providers;
    private final HashMap<String, User> users;
    private final ObjectMapper mapper;

    public CommodityProvisionSystem() {
        commodities = new HashMap<>();
        providers = new HashMap<>();
        users = new HashMap<>();
        mapper = new ObjectMapper();
    }

    JsonNode createJsonResult(boolean success, JsonNode data) {
        ObjectNode root = mapper.createObjectNode();
        root.put("success", success);
        root.set("data", data);
        return root;
    }

    public Provider findProvider(Integer providerId) throws ProviderNotFound {
        if (!providers.containsKey(providerId)) {
            throw new ProviderNotFound(providerId);
        }
        return providers.get(providerId);
    }

    public User findUser(String username) throws UserNotFound {
        if (!users.containsKey(username)) {
            throw new UserNotFound(username);
        }
        return users.get(username);
    }

    public Commodity findCommodity(Integer commodityId) throws CommodityNotFound {
        if (!commodities.containsKey(commodityId)) {
            throw new CommodityNotFound(commodityId);
        }
        return commodities.get(commodityId);
    }

    public JsonNode addUser(JsonNode userNode) {
        ObjectNode response = mapper.createObjectNode();
        String responseText;
        boolean success = true;
        try {
            User user = mapper.treeToValue(userNode, User.class);
            user.validate();
            users.put(user.getUsername(), user);
            responseText = "User with username " + user.getUsername() + " added/updated successfully!";
        } catch (Exception e) {
            responseText = e.getMessage();
            success = false;
        }
        response.put("response", responseText);
        return createJsonResult(success, response);
    }

    public JsonNode getUserById(JsonNode userNode) {
        ObjectNode response;
        boolean success = true;
        try {
            User user = findUser(userNode.get("username").asText());
            response = user.getObjectNode();
        } catch (Exception e) {
            response = mapper.createObjectNode();
            response.put("response", e.getMessage());
            success = false;
        }
        return createJsonResult(success, response);
    }

    public JsonNode addCredit(JsonNode userNode) {
        ObjectNode response = mapper.createObjectNode();
        String responseText;
        boolean success = true;
        try {
            User user = findUser(userNode.get("username").asText());
            user.addCredit(userNode.get("credit").asInt());
            responseText = "Credit added to user " + user.getUsername() + " successfully!";
        } catch (Exception e) {
            responseText = e.getMessage();
            success = false;
        }
        response.put("response", responseText);
        return createJsonResult(success, response);
    }

    public JsonNode addProvider(JsonNode providerNode) {
        ObjectNode response = mapper.createObjectNode();
        String responseText;
        boolean success = true;
        try {
            Provider provider = mapper.treeToValue(providerNode, Provider.class);
            providers.put(provider.getId(), provider);
            responseText = "Provider with id " + provider.getId() + " added/updated successfully!";
        } catch (Exception e) {
            responseText = e.getMessage();
            success = false;
        }
        response.put("response", responseText);
        return createJsonResult(success, response);
    }

    public JsonNode getProviderById(JsonNode providerNode) {
        ObjectNode response;
        boolean success = true;
        try {
            Provider provider = findProvider(providerNode.get("id").asInt());
            response = provider.getObjectNode();
        } catch (Exception e) {
            response = mapper.createObjectNode();
            response.put("response", e.getMessage());
            success = false;
        }
        return createJsonResult(success, response);
    }

    public JsonNode addCommodity(JsonNode commodityNode) {
        ObjectNode response = mapper.createObjectNode();
        String responseText;
        boolean success = true;
        try {
            Commodity commodity = mapper.treeToValue(commodityNode, Commodity.class);
            findProvider(commodity.getProviderId());
            commodities.put(commodity.getId(), commodity);
            responseText = "Commodity with id " + commodity.getId() + " added/updated successfully!";
        } catch (Exception e) {
            responseText = e.getMessage();
            success = false;
        }
        response.put("response", responseText);
        return createJsonResult(success, response);
    }

    public JsonNode getCommoditiesList() {
        ArrayList<ObjectNode> commoditiesNodes = new ArrayList<>();
        for (Commodity commodity : commodities.values()) {
            commoditiesNodes.add(commodity.getObjectNode());
        }
        return createJsonResult(true, mapper.valueToTree(commoditiesNodes));
    }

    public JsonNode getCommodityById(JsonNode commodityNode) {
        ObjectNode response;
        boolean success = true;
        try {
            Commodity commodity = findCommodity(commodityNode.get("id").asInt());
            Provider provider = findProvider(commodity.getProviderId());
            response = commodity.getObjectNode();
            response.remove("providerId");
            response.put("provider", provider.getName());
        } catch (Exception e) {
            response = mapper.createObjectNode();
            response.put("response", e.getMessage());
            success = false;
        }
        return createJsonResult(success, response);
    }

    public JsonNode getCommoditiesByCategory(JsonNode commodityNode) {
        ArrayList<ObjectNode> commoditiesNode = new ArrayList<>();
        for (Commodity commodity : commodities.values()) {
            if (commodity.isInCategory(commodityNode.get("category").asText())) {
                commoditiesNode.add(commodity.getObjectNode());
            }
        }
        ObjectNode response = mapper.createObjectNode();
        response.set("CommoditiesListByCategory", mapper.valueToTree(commoditiesNode));
        return createJsonResult(true, response);
    }

    public JsonNode rateCommodity(JsonNode ratingNode) {
        ObjectNode response = mapper.createObjectNode();
        String responseText;
        boolean success = true;
        try {
            User user = findUser(ratingNode.get("username").asText());
            Commodity commodity = findCommodity(ratingNode.get("commodityId").asInt());
            commodity.addUserRating(user.getUsername(), ratingNode.get("score").asText());
            responseText = "Commodity with id " + commodity.getId() + " rated by user with username " + user.getUsername() + " successfully!";
        } catch (Exception e) {
            responseText = e.getMessage();
            success = false;
        }
        response.put("response", responseText);
        return createJsonResult(success, response);
    }

    public JsonNode addToBuyList(JsonNode buyListNode) {
        ObjectNode response = mapper.createObjectNode();
        String responseText;
        boolean success = true;
        try {
            User user = findUser(buyListNode.get("username").asText());
            Commodity commodity = findCommodity(buyListNode.get("commodityId").asInt());
            commodity.validate();
            user.addToBuyList(commodity);
            commodity.updateStock(-1);
            responseText = "Commodity with id " + commodity.getId() + " added to user's buy list with username " + user.getUsername() + " successfully!";
        } catch (Exception e) {
            responseText = e.getMessage();
            success = false;
        }
        response.put("response", responseText);
        return createJsonResult(success, response);
    }

    public JsonNode removeFromBuyList(JsonNode buyListNode) {
        ObjectNode response = mapper.createObjectNode();
        String responseText;
        boolean success = true;
        try {
            User user = findUser(buyListNode.get("username").asText());
            Commodity commodity = findCommodity(buyListNode.get("commodityId").asInt());
            user.removeFromBuyList(commodity);
            commodity.updateStock(1);
            responseText = "Commodity with id " + commodity.getId() + " removed from user's buy list with username " + user.getUsername() + " successfully!";
        } catch (Exception e) {
            responseText = e.getMessage();
            success = false;
        }
        response.put("response", responseText);
        return createJsonResult(success, response);
    }

    public JsonNode getBuyList(JsonNode buyListNode) {
        ObjectNode response = mapper.createObjectNode();
        boolean success = true;
        try {
            User user = findUser(buyListNode.get("username").asText());
            response.set("buyList", mapper.valueToTree(user.getBuyList()));
        } catch (Exception e) {
            response.put("response", e.getMessage());
            success = false;
        }
        return createJsonResult(success, response);
    }
}
