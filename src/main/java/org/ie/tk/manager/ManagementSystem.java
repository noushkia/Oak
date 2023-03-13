package org.ie.tk.manager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ie.tk.database.DataBase;

public class ManagementSystem {
    DataBase db = new DataBase();
    ObjectMapper mapper = new ObjectMapper();

    public ManagementSystem(DataBase db) {
        this.db = db;
    }

    public JsonNode createJsonResult(boolean success, JsonNode data) {
        ObjectNode root = mapper.createObjectNode();
        root.put("success", success);
        root.set("data", data);
        return root;
    }

}
