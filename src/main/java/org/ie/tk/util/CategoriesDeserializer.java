package org.ie.tk.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class CategoriesDeserializer extends JsonDeserializer<ArrayList<String>> {
    @Override
    public ArrayList<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(p);
        String categoriesString = node.asText();
        // remove the brackets and split the string by comma
        String[] categoriesArray = categoriesString.substring(1, categoriesString.length() - 1).split(", ");
        return new ArrayList<>(Arrays.asList(categoriesArray));
    }
}
