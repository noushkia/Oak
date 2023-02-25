package org.ie.tk;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Scanner;
public class CommandHandler {
    private static final int COMMAND_INDEX = 0;
    private static final int JSON_INDEX = 1;
    private static final String ADD_USER = "addUser";
    private static final String ADD_PROVIDER = "addProvider";
    private static final String ADD_COMMODITY = "addCommodity";
    private static final String GET_COMMODITIES_LIST = "getCommoditiesList";
    private static final String RATE_COMMODITY = "rateCommodity";
    private static final String ADD_TO_BUY_LIST = "addToBuyList";
    private static final String REMOVE_FROM_BUY_LIST = "removeFromBuyList";
    private static final String GET_COMMODITY_BY_ID = "getCommodityById";
    private static final String GET_COMMODITIES_BY_CATEGORY = "getCommoditiesByCategory";
    private static final String GET_BUY_LIST = "getBuyList";

    private final CommodityProvisionSystem commodityProvisionSystem = new CommodityProvisionSystem();
    private ObjectMapper mapper = new ObjectMapper();

    private void printJson(JsonNode json) {
        try {
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
    public void run() throws JsonProcessingException {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String input = scanner.nextLine();
            String[] splitInput = input.split(" ", 2);
            JsonNode jsonNode = null, responseNode = null;
            if (splitInput.length > 1) {
                jsonNode = mapper.readTree(splitInput[JSON_INDEX]);
            }
            switch (splitInput[COMMAND_INDEX]) {
                case ADD_USER -> {
                    responseNode = commodityProvisionSystem.addUser(jsonNode);
                    break;
                }
                case ADD_PROVIDER -> {
                    responseNode = commodityProvisionSystem.addProvider(jsonNode);
                    break;
                }
                case ADD_COMMODITY -> {
                    responseNode = commodityProvisionSystem.addCommodity(jsonNode);
                    break;
                }
                case GET_COMMODITIES_LIST -> {
                    responseNode = commodityProvisionSystem.getCommoditiesList();
                    break;
                }
                case RATE_COMMODITY -> {
                    responseNode = commodityProvisionSystem.rateCommodity(jsonNode);
                    break;
                }
                case ADD_TO_BUY_LIST -> {
                    responseNode = commodityProvisionSystem.addToBuyList(jsonNode);
                    break;
                }
                case REMOVE_FROM_BUY_LIST -> {
                    responseNode = commodityProvisionSystem.removeFromBuyList(jsonNode);
                    break;
                }
                case GET_COMMODITY_BY_ID -> {
                    responseNode = commodityProvisionSystem.getCommodityById(jsonNode);
                    break;
                }
                case GET_COMMODITIES_BY_CATEGORY -> {
                    responseNode = commodityProvisionSystem.getCommoditiesByCategory(jsonNode);
                    break;
                }
                case GET_BUY_LIST -> {
                    responseNode = commodityProvisionSystem.getBuyList(jsonNode);
                    break;
                }
            }
            if (responseNode != null){
                printJson(responseNode);
            }
        }
    }
    public static void main(String[] args) throws JsonProcessingException {
        CommandHandler commandHandler = new CommandHandler();
        commandHandler.run();
    }
}
