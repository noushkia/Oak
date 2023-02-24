package org.ie.tk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Scanner;
public class CommandHandler {
    // Consts
    private static final int COMMAND_INDEX = 0;
    private static final int JSON_INDEX = 1;
    private static final String ADD_USER = "addUser";
    private static final String ADD_PROVIDER = "addProvider";
    private static final String ADD_COMMODITY = "addCommodity";

    private final CommodityProvisionSystem commodityProvisionSystem = new CommodityProvisionSystem();

    private void printJson(JsonNode json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
    public void run() throws JsonProcessingException {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String input = scanner.nextLine();
            String[] splitInput = input.split(" ", 2);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = null;
            if (splitInput.length > 1) {
                jsonNode = mapper.readTree(splitInput[JSON_INDEX]);
            }
            switch (splitInput[COMMAND_INDEX]) {
                case ADD_USER -> {
                    printJson(commodityProvisionSystem.addUser(jsonNode));
                    break;
                }
                case ADD_PROVIDER -> {
                    printJson(commodityProvisionSystem.addProvider(jsonNode));
                    break;
                }
                case ADD_COMMODITY -> {
                    printJson(commodityProvisionSystem.addCommodity(jsonNode));
                    break;
                }
            }
        }
    }
}
