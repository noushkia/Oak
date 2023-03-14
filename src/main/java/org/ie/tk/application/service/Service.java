package org.ie.tk.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ie.tk.data.Database;

public class Service {
    Database db;
    ObjectMapper mapper = new ObjectMapper();

    public Service(Database db) {
        this.db = db;
    }

    public JsonNode createJsonResult(boolean success, JsonNode data) {
        ObjectNode root = mapper.createObjectNode();
        root.put("success", success);
        root.set("data", data);
        return root;
    }

}
