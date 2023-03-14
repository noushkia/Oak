package org.ie.tk.presentation.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ie.tk.application.service.ServiceLayer;

public abstract class JsonPresentation {

    protected static ObjectMapper mapper = new ObjectMapper();
    protected ServiceLayer serviceLayer;

    public JsonPresentation(ServiceLayer serviceLayer){
        this.serviceLayer = serviceLayer;
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
