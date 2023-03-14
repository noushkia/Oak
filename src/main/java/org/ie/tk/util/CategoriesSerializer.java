package org.ie.tk.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.util.ArrayList;

public class CategoriesSerializer extends JsonSerializer<ArrayList<String>> {
    @Override
    public void serialize(ArrayList<String> strings, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String categoriesString = String.join(", ", strings);
        jsonGenerator.writeString("[" + categoriesString + "]");
    }
}

