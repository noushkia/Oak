package org.ie.tk.presentation.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ie.tk.application.service.ServiceLayer;
import org.ie.tk.domain.Provider;

public class ProviderJsonPresentation extends JsonPresentation {
    public ProviderJsonPresentation(ServiceLayer serviceLayer) {
        super(serviceLayer);
    }

    public static ObjectNode marshalProviderObject(Provider provider) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode providerNode = objectMapper.createObjectNode();
        providerNode.put("id", provider.getId());
        providerNode.put("name", provider.getName());
        return providerNode;
    }

    public JsonNode addProvider(String data) throws JsonProcessingException {
        JsonNode providerNode = fetchData(data);
        ObjectNode response = mapper.createObjectNode();
        String responseText;
        boolean success = true;
        try {
            Provider provider = mapper.treeToValue(providerNode, Provider.class);
            serviceLayer.getProviderService().addProvider(provider);
            responseText = "Provider with id " + provider.getId() + " added/updated successfully!";
        } catch (Exception e) {
            responseText = e.getMessage();
            success = false;
        }
        response.put("response", responseText);
        return marshalResponse(success, response);
    }

    public JsonNode getProviderById(String data) throws JsonProcessingException {
        JsonNode providerNode = fetchData(data);
        ObjectNode response;
        boolean success = true;
        try {
            Provider provider = serviceLayer.getProviderService().getProviderById(providerNode.get("id").asInt());
            response = marshalProviderObject(provider);
        } catch (Exception e) {
            response = mapper.createObjectNode();
            response.put("response", e.getMessage());
            success = false;
        }
        return marshalResponse(success, response);
    }
}
