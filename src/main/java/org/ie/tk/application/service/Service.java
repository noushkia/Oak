package org.ie.tk.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ie.tk.data.Database;

public abstract class Service {
    protected Database db;
    protected ObjectMapper mapper = new ObjectMapper();

    public Service(Database db) {
        this.db = db;
    }
}
