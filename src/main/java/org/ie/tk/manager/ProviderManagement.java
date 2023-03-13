package org.ie.tk.manager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ie.tk.database.DataBase;
import org.ie.tk.model.Provider;

public class ProviderManagement extends ManagementSystem {
    public ProviderManagement(DataBase db) {
        super(db);
    }

    public JsonNode addProvider(JsonNode providerNode) {
        ObjectNode response = mapper.createObjectNode();
        String responseText;
        boolean success = true;
        try {
            Provider provider = mapper.treeToValue(providerNode, Provider.class);
            db.addProvider(provider);
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
            Provider provider = db.fetchProvider(providerNode.get("id").asInt());
            response = provider.getObjectNode();
        } catch (Exception e) {
            response = mapper.createObjectNode();
            response.put("response", e.getMessage());
            success = false;
        }
        return createJsonResult(success, response);
    }

}
