package org.ie.tk.manager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jdk.jshell.spi.ExecutionControl;
import org.ie.tk.database.DataBase;
import org.ie.tk.model.Commodity;
import org.ie.tk.model.Provider;
import org.ie.tk.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommodityManagement extends ManagementSystem {

    public CommodityManagement(DataBase db) {
        super(db);
    }


    public JsonNode addCommodity(JsonNode commodityNode) {
        ObjectNode response = mapper.createObjectNode();
        String responseText;
        boolean success = true;
        try {
            Commodity commodity = mapper.treeToValue(commodityNode, Commodity.class);
            // todo: check
            db.fetchProvider(commodity.getProviderId());
            db.addCommodity(commodity);
            responseText = "Commodity with id " + commodity.getId() + " added/updated successfully!";
        } catch (Exception e) {
            responseText = e.getMessage();
            success = false;
        }
        response.put("response", responseText);
        return createJsonResult(success, response);
    }


    public JsonNode getCommoditiesList() {
        List<ObjectNode> commodityNodes = db.fetchCommodities(c -> true).stream().map(Commodity::getObjectNode).collect(Collectors.toList());
        return createJsonResult(true, mapper.valueToTree(commodityNodes));
    }

    public JsonNode getCommodityById(JsonNode commodityNode) {
        ObjectNode response;
        boolean success = true;
        try {
            Commodity commodity = db.fetchCommodity(commodityNode.get("id").asInt());
            Provider provider = db.fetchProvider(commodity.getProviderId());
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
        String category = commodityNode.get("category").asText();
        List<ObjectNode> commodityNodes = db.fetchCommodities(c -> c.containsCategory(category)).stream().map(Commodity::getObjectNode).collect(Collectors.toList());
        return createJsonResult(true, mapper.valueToTree(commodityNodes));
    }

    public JsonNode rateCommodity(JsonNode ratingNode) {
        ObjectNode response = mapper.createObjectNode();
        String responseText;
        boolean success = true;
        try {
            User user = db.fetchUser(ratingNode.get("username").asText());
            Commodity commodity = db.fetchCommodity(ratingNode.get("commodityId").asInt());
            commodity.addUserRating(user.getUsername(), ratingNode.get("score").asText());
            responseText = "Commodity with id " + commodity.getId() + " rated by user with username " + user.getUsername() + " successfully!";
        } catch (Exception e) {
            responseText = e.getMessage();
            success = false;
        }
        response.put("response", responseText);
        return createJsonResult(success, response);
    }

    public JsonNode voteComment(JsonNode voteNode) throws ExecutionControl.NotImplementedException {
        // todo
        throw new ExecutionControl.NotImplementedException("vote comment");
    }

}
