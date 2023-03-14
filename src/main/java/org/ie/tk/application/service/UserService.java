package org.ie.tk.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ie.tk.data.Database;
import org.ie.tk.exception.User.InvalidUsername;
import org.ie.tk.domain.Commodity;
import org.ie.tk.domain.User;

public class UserService extends Service {

    public UserService(Database db) {
        super(db);
    }

    public void setUser(User user) throws InvalidUsername {
        user.validate();
        db.addUser(user);
    }

    public JsonNode addUser(JsonNode userNode) {
        ObjectNode response = mapper.createObjectNode();
        String responseText;
        boolean success = true;
        try {
            User user = mapper.treeToValue(userNode, User.class);
            setUser(user);
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
            User user = db.fetchUser(userNode.get("username").asText());
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
            User user = db.fetchUser(userNode.get("username").asText());
            user.addCredit(userNode.get("credit").asInt());
            responseText = "Credit added to user " + user.getUsername() + " successfully!";
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
            User user = db.fetchUser(buyListNode.get("username").asText());
            Commodity commodity = db.fetchCommodity(buyListNode.get("commodityId").asInt());
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
            User user = db.fetchUser(buyListNode.get("username").asText());
            Commodity commodity = db.fetchCommodity(buyListNode.get("commodityId").asInt());
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
            User user = db.fetchUser(buyListNode.get("username").asText());
            response.set("buyList", mapper.valueToTree(user.getBuyList()));
        } catch (Exception e) {
            response.put("response", e.getMessage());
            success = false;
        }
        return createJsonResult(success, response);
    }

}
