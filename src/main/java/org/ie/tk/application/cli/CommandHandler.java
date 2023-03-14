package org.ie.tk.application.cli;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ie.tk.application.service.ServiceLayer;
import org.ie.tk.data.Database;
import org.ie.tk.presentation.json.JsonPresentationLayer;

import java.util.Scanner;

public class CommandHandler {
    private static final int COMMAND_INDEX = 0;
    private static final int DATA_INDEX = 1;
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
    private final Database database = new Database();
    private final ServiceLayer serviceLayer = new ServiceLayer(database);
    private final JsonPresentationLayer jsonPresentationLayer = new JsonPresentationLayer(serviceLayer);
    private final ObjectMapper mapper = new ObjectMapper();

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
            JsonNode responseNode = null;
            switch (splitInput[COMMAND_INDEX]) {
                case ADD_USER -> {
                    responseNode = jsonPresentationLayer.getUserJsonPresentation().addUser(splitInput[DATA_INDEX]);
                }
                case ADD_PROVIDER -> {
                    responseNode = jsonPresentationLayer.getProviderJsonPresentation().addProvider(splitInput[DATA_INDEX]);
                }
                case ADD_COMMODITY -> {
                    responseNode = jsonPresentationLayer.getCommodityJsonPresentation().addCommodity(splitInput[DATA_INDEX]);
                }
                case GET_COMMODITIES_LIST -> {
                    responseNode = jsonPresentationLayer.getCommodityJsonPresentation().getCommoditiesList();
                }
                case RATE_COMMODITY -> {
                    responseNode = jsonPresentationLayer.getCommodityJsonPresentation().rateCommodity(splitInput[DATA_INDEX]);
                }
                case ADD_TO_BUY_LIST -> {
                    responseNode = jsonPresentationLayer.getUserJsonPresentation().addToBuyList(splitInput[DATA_INDEX]);
                }
                case REMOVE_FROM_BUY_LIST -> {
                    responseNode = jsonPresentationLayer.getUserJsonPresentation().removeFromBuyList(splitInput[DATA_INDEX]);
                }
                case GET_COMMODITY_BY_ID -> {
                    responseNode = jsonPresentationLayer.getCommodityJsonPresentation().getCommodityById(splitInput[DATA_INDEX]);
                }
                case GET_COMMODITIES_BY_CATEGORY -> {
                    responseNode = jsonPresentationLayer.getCommodityJsonPresentation().getCommoditiesByCategory(splitInput[DATA_INDEX]);
                }
                case GET_BUY_LIST -> {
                    responseNode = jsonPresentationLayer.getUserJsonPresentation().getBuyList(splitInput[DATA_INDEX]);
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
