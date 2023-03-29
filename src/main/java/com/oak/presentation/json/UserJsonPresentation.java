package com.oak.presentation.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.oak.application.service.ServiceLayer;
import com.oak.domain.Commodity;
import com.oak.domain.User;

import java.util.List;

public class UserJsonPresentation extends JsonPresentation{

    public UserJsonPresentation(ServiceLayer serviceLayer){
        super(serviceLayer);
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
        return marshalResponse(success, response);
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
        return marshalResponse(success, response);
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
        return marshalResponse(success, response);
    }

    public JsonNode getBuyList(String data) throws JsonProcessingException {
        JsonNode buyListNode = fetchData(data);
        ObjectNode response = mapper.createObjectNode();
        boolean success = true;
        try {
            List<Commodity> buyList = serviceLayer.getUserService().getBuyList(buyListNode.get("username").asText());
            response.set("buyList", mapper.valueToTree(CommodityJsonPresentation.marshalCommodityObjects(buyList)));
        } catch (Exception e) {
            response.put("response", e.getMessage());
            success = false;
        }
        return marshalResponse(success, response);
    }
}
