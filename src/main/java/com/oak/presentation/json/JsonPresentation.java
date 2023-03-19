package com.oak.presentation.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.oak.application.service.ServiceLayer;
import com.oak.presentation.Presentation;

public abstract class JsonPresentation extends Presentation {

    protected static ObjectMapper mapper = new ObjectMapper();

    public JsonPresentation(ServiceLayer serviceLayer) {
        super(serviceLayer);
    }

    public JsonNode fetchData(String data) throws JsonProcessingException {
        return mapper.readTree(data);
    }
    public JsonNode marshalResponse(boolean success, JsonNode data) {
        ObjectNode root = mapper.createObjectNode();
        root.put("success", success);
        root.set("data", data);
        return root;
    }
}
