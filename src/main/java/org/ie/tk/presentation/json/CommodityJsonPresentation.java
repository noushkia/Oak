package org.ie.tk.presentation.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jdk.jshell.spi.ExecutionControl;
import org.ie.tk.application.service.ServiceLayer;
import org.ie.tk.domain.Commodity;
import org.ie.tk.domain.Provider;

import java.util.List;
import java.util.stream.Collectors;

public class CommodityJsonPresentation extends JsonPresentation {

    public CommodityJsonPresentation(ServiceLayer serviceLayer) {
        super(serviceLayer);
    }

    public static ObjectNode marshalCommodityObject(Commodity commodity) {
        ObjectNode commodityNode = mapper.createObjectNode();
        commodityNode.put("id", commodity.getId());
        commodityNode.put("name", commodity.getName());
        commodityNode.put("providerId", commodity.getProviderId());
        commodityNode.put("price", commodity.getPrice());
        commodityNode.set("categories", mapper.valueToTree(commodity.getCategories()));
        commodityNode.put("rating", commodity.getRating());
        return commodityNode;
    }

    public static List<ObjectNode> marshallCommodityObjects(List<Commodity> commodities) {
        return commodities.stream()
                .map(CommodityJsonPresentation::marshalCommodityObject)
                .collect(Collectors.toList());
    }


    public JsonNode addCommodity(String data) throws JsonProcessingException {
        ObjectNode response = mapper.createObjectNode();
        String responseText;
        JsonNode commodityNode = fetchData(data);
        boolean success = true;
        try {
            Commodity commodity = mapper.treeToValue(commodityNode, Commodity.class);
            serviceLayer.getCommodityService().addCommodity(commodity);
            responseText = "Commodity with id " + commodity.getId() + " added/updated successfully!";
        } catch (Exception e) {
            responseText = e.getMessage();
            success = false;
        }
        response.put("response", responseText);
        return marshalResponse(success, response);
    }


    public JsonNode getCommoditiesList() {
        List<ObjectNode> commodityNodes = serviceLayer.getCommodityService().getCommoditiesList()
                .stream().map(CommodityJsonPresentation::marshalCommodityObject)
                .collect(Collectors.toList());
        return marshalResponse(true, mapper.valueToTree(commodityNodes));
    }

    public JsonNode getCommodityById(String data) throws JsonProcessingException {
        ObjectNode response;
        JsonNode commodityNode = fetchData(data);
        boolean success = true;
        try {
            Commodity commodity = serviceLayer.getCommodityService().getCommodityById(commodityNode.get("id").asInt());
            Provider provider = serviceLayer.getProviderService().getProviderById(commodity.getProviderId());
            response = marshalCommodityObject(commodity);
            response.remove("providerId");
            response.put("provider", provider.getName());
        } catch (Exception e) {
            response = mapper.createObjectNode();
            response.put("response", e.getMessage());
            success = false;
        }
        return marshalResponse(success, response);
    }

    public JsonNode getCommoditiesByCategory(String data) throws JsonProcessingException {
        JsonNode commodityNode = fetchData(data);
        String category = commodityNode.get("category").asText();
        List<ObjectNode> commodityNodes = serviceLayer.getCommodityService().getCommoditiesByCategory(category)
                .stream().map(CommodityJsonPresentation::marshalCommodityObject)
                .collect(Collectors.toList());
        return marshalResponse(true, mapper.valueToTree(commodityNodes));
    }

    public JsonNode rateCommodity(String data) throws JsonProcessingException {
        JsonNode ratingNode = fetchData(data);
        ObjectNode response = mapper.createObjectNode();
        String responseText;
        boolean success = true;
        try {
            String username = ratingNode.get("username").asText();
            int commodityId = ratingNode.get("commodityId").asInt();
            String rating = ratingNode.get("score").asText();
            serviceLayer.getCommodityService().rateCommodity(username, commodityId, rating);
            responseText = "Commodity with id " + commodityId + " rated by user with username " + username + " successfully!";
        } catch (Exception e) {
            responseText = e.getMessage();
            success = false;
        }
        response.put("response", responseText);
        return marshalResponse(success, response);
    }

    public JsonNode voteComment(JsonNode voteNode) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("vote comment");
    }

}
