package com.oak.presentation.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.oak.application.service.ServiceLayer;
import com.oak.domain.Provider;

public class ProviderJsonPresentation extends JsonPresentation {
    public ProviderJsonPresentation(ServiceLayer serviceLayer) {
        super(serviceLayer);
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
}
