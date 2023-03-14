package org.ie.tk.presentation.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ie.tk.application.service.ServiceLayer;
import org.ie.tk.domain.Commodity;
import org.ie.tk.domain.User;

import java.util.List;

public class UserJsonPresentation extends JsonPresentation{

    public UserJsonPresentation(ServiceLayer serviceLayer){
        super(serviceLayer);
    }

    public static ObjectNode marshallUserObject(User user){
        ObjectNode userNode = mapper.createObjectNode();
        userNode.put("username", user.getUsername());
        userNode.put("email", user.getEmail());
        return userNode;
    }

    public JsonNode addUser(String data) throws JsonProcessingException {
        ObjectNode response = mapper.createObjectNode();
        String responseText;
        JsonNode userNode = fetchData(data);
        boolean success = true;
        try {
            User user = mapper.treeToValue(userNode, User.class);
            serviceLayer.getUserService().addUser(user);
            responseText = "User with username " + user.getUsername() + " added/updated successfully!";
        } catch (Exception e) {
            responseText = e.getMessage();
            success = false;
        }
        response.put("response", responseText);
        return marshallResponse(success, response);
    }

    public JsonNode getUserById(String data) throws JsonProcessingException {
        ObjectNode response;
        JsonNode userNode = fetchData(data);
        boolean success = true;
        try {
            User user = serviceLayer.getUserService().getUserById(userNode.get("username").asText());
            response = marshallUserObject(user);
        } catch (Exception e) {
            response = mapper.createObjectNode();
            response.put("response", e.getMessage());
            success = false;
        }
        return marshallResponse(success, response);
    }

    public JsonNode addCredit(String data) throws JsonProcessingException {
        JsonNode userNode = fetchData(data);
        ObjectNode response = mapper.createObjectNode();
        String responseText;
        boolean success = true;
        try {
            String username = userNode.get("username").asText();
            serviceLayer.getUserService().addCredit(username, userNode.get("credit").asInt());
            responseText = "Credit added to user " + username + " successfully!";
        } catch (Exception e) {
            responseText = e.getMessage();
            success = false;
        }
        response.put("response", responseText);
        return marshallResponse(success, response);
    }

    public JsonNode addToBuyList(String data) throws JsonProcessingException {
        JsonNode buyListNode = fetchData(data);
        ObjectNode response = mapper.createObjectNode();
        String responseText;
        boolean success = true;
        try {
            String username = buyListNode.get("username").asText();
            Integer commodityId = buyListNode.get("commodityId").asInt();
            serviceLayer.getUserService().addToBuyList(username, commodityId);
            responseText = "Commodity with id " + commodityId + " added to user's buy list with username " + username + " successfully!";
        } catch (Exception e) {
            responseText = e.getMessage();
            success = false;
        }
        response.put("response", responseText);
        return marshallResponse(success, response);
    }

    public JsonNode removeFromBuyList(String data) throws JsonProcessingException {
        JsonNode buyListNode = fetchData(data);
        ObjectNode response = mapper.createObjectNode();
        String responseText;
        boolean success = true;
        try {
            String username = buyListNode.get("username").asText();
            Integer commodityId = buyListNode.get("commodityId").asInt();
            serviceLayer.getUserService().removeFromBuyList(username, commodityId);
            responseText = "Commodity with id " + commodityId + " removed from user's buy list with username " + username + " successfully!";
        } catch (Exception e) {
            responseText = e.getMessage();
            success = false;
        }
        response.put("response", responseText);
        return marshallResponse(success, response);
    }

    public JsonNode getBuyList(String data) throws JsonProcessingException {
        JsonNode buyListNode = fetchData(data);
        ObjectNode response = mapper.createObjectNode();
        boolean success = true;
        try {
            List<Commodity> buyList = serviceLayer.getUserService().getBuyList(buyListNode.get("username").asText());
            response.set("buyList", mapper.valueToTree(CommodityJsonPresentation.marshallCommodityObjects(buyList)));
        } catch (Exception e) {
            response.put("response", e.getMessage());
            success = false;
        }
        return marshallResponse(success, response);
    }
}
