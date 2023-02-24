package org.ie.tk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ie.tk.Exception.Provider.ProviderNotFound;
import org.ie.tk.Exception.User.InvalidUsername;

import java.util.HashMap;

public class CommodityProvisionSystem {

    private HashMap<String, Commodity> commodities;
    private HashMap<String, Provider> providers;
    private HashMap<String, User> users;

    public CommodityProvisionSystem() {
        commodities = new HashMap<>();
        providers = new HashMap<>();
        users = new HashMap<>();
    }

    private JsonNode createJsonResult(boolean success, JsonNode data) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode root = objectMapper.createObjectNode();
        root.put("success", success);
        root.set("data", data);
        return root;
    }

    public JsonNode addUser(JsonNode userNode) {
        ObjectMapper mapper = new ObjectMapper();
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

    public JsonNode addProvider(JsonNode providerNode) {
        ObjectMapper mapper = new ObjectMapper();
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

    public void checkIfProviderExists(String providerId) throws ProviderNotFound {
        if (!providers.containsKey(providerId)) {
            throw new ProviderNotFound(providerId);
        }
    }

    public JsonNode addCommodity(JsonNode commodityNode) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        String responseText;
        boolean success = true;
        try {
            Commodity commodity = mapper.treeToValue(commodityNode, Commodity.class);
            checkIfProviderExists(commodity.getProviderId());
            commodities.put(commodity.getId(), commodity);
            responseText = "Commodity with id " + commodity.getId() + " added/updated successfully!";
        } catch (Exception e) {
            responseText = e.getMessage();
            success = false;
        }
        response.put("response", responseText);
        return createJsonResult(success, response);
    }



}
