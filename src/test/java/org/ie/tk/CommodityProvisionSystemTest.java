package org.ie.tk;

import static org.junit.Assert.*;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.TextNode;
import org.ie.tk.exception.Commodity.CommodityNotFound;
import org.ie.tk.domain.Commodity;
import org.ie.tk.domain.Provider;
import org.ie.tk.domain.User;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class CommodityProvisionSystemTest {
    private CommodityProvisionSystem cps;
    private static ObjectMapper mapper;

    static User[] users;
    static Provider[] providers;
    static Commodity[] commodities;

    @BeforeClass
    public static void initialSetup() throws IOException {
        mapper = new ObjectMapper();
        users = mapper.readValue(new File("src/test/resources/users.json"), User[].class);
        providers = mapper.readValue(new File("src/test/resources/providers.json"), Provider[].class);
        commodities = mapper.readValue(new File("src/test/resources/commodities.json"), Commodity[].class);
    }

    @Before
    public void setup() {
        cps = new CommodityProvisionSystem();
        for (User user : users) {
            ObjectNode userNode = mapper.valueToTree(user);
            cps.addUser(userNode);
        }
        for (Provider provider : providers) {
            ObjectNode providerNode = mapper.valueToTree(provider);
            cps.addProvider(providerNode);
        }
        for (Commodity commodity : commodities) {
            ObjectNode commodityNode = mapper.valueToTree(commodity);
            cps.addCommodity(commodityNode);
        }
    }

    // rate commodity tests
    @Test
    public void rateCommodity_withValidInputs_shouldAddRating() throws Exception {
        // Arrange
        String username = users[0].getUsername();
        Integer commodityId = commodities[0].getId();
        ObjectNode ratingNode = mapper.createObjectNode();
        ratingNode.put("username", username);
        ratingNode.put("commodityId", commodityId);
        ratingNode.put("score", 4);
        JsonNode result = cps.rateCommodity(ratingNode);

        // Verify the rating was successful
        assertTrue(result.get("success").asBoolean());

        // Get the commodity and verify the rating was added
        Commodity commodity = cps.findCommodity(commodityId);
        assertTrue(commodity.getUserRatings().containsKey(username));
        assertEquals(4, commodity.getUserRatings().get(username).intValue());
    }

    @Test
    public void rateCommodity_withInvalidType_shouldGetInvalidRating() {
        // Arrange
        String username = users[0].getUsername();
        Integer commodityId = commodities[0].getId();
        ObjectNode ratingNode = mapper.createObjectNode();
        ratingNode.put("username", username);
        ratingNode.put("commodityId", commodityId);
        ratingNode.put("score",  4.5);
        JsonNode result = cps.rateCommodity(ratingNode);

        // Verify the rating was failed
        assertFalse(result.get("success").asBoolean());
    }

    @Test
    public void rateCommodity_outOfBoundRating_shouldGetInvalidRating() {
        // Arrange
        String username = users[0].getUsername();
        Integer commodityId = commodities[0].getId();
        ObjectNode ratingNode = mapper.createObjectNode();
        ratingNode.put("username", username);
        ratingNode.put("commodityId", commodityId);
        ratingNode.put("score",  100);
        JsonNode result = cps.rateCommodity(ratingNode);

        // Verify the rating was failed
        assertFalse(result.get("success").asBoolean());
    }

    @Test
    public void rateCommodity_duplicateRating_shouldUpdateRating() throws CommodityNotFound {
        // Arrange
        String username = users[0].getUsername();
        Integer commodityId = commodities[0].getId();
        ObjectNode ratingNode = mapper.createObjectNode();
        ratingNode.put("username", username);
        ratingNode.put("commodityId", commodityId);
        ratingNode.put("score",  3);
        cps.rateCommodity(ratingNode);

        ratingNode = mapper.createObjectNode();
        ratingNode.put("username", username);
        ratingNode.put("commodityId", commodityId);
        ratingNode.put("score",  7);
        JsonNode result = cps.rateCommodity(ratingNode);

        // Verify the rating was updated
        assertEquals(cps.findCommodity(commodityId).getUserRatings().get(username).intValue(), 7);
    }

    @Test
    public void rateCommodity_withNonExistingUser_shouldGetUserNotFound() {
        // Arrange
        Integer commodityId = commodities[0].getId();
        ObjectNode ratingNode = mapper.createObjectNode();
        ratingNode.put("username", "Omidak");
        ratingNode.put("commodityId", commodityId);
        ratingNode.put("score",  5);
        JsonNode result = cps.rateCommodity(ratingNode);

        // Verify the rating was failed
        assertFalse(result.get("success").asBoolean());
    }

    @Test
    public void rateCommodity_withNonExistingCommodity_shouldGetCommodityNotFound() {
        // Arrange
        String username = users[0].getUsername();
        ObjectNode ratingNode = mapper.createObjectNode();
        ratingNode.put("username", username);
        ratingNode.put("commodityId", 100);
        ratingNode.put("score",  5);
        JsonNode result = cps.rateCommodity(ratingNode);

        // Verify the rating was failed
        assertFalse(result.get("success").asBoolean());
    }

    // getCommodityById tests
    @Test
    public void getCommodityById_withValidInputs_shouldGetCommodity() {
        // Arrange
        Integer id = commodities[0].getId();
        ObjectNode idNode = mapper.createObjectNode();
        idNode.put("id", id);

        // Act
        JsonNode result = cps.getCommodityById(idNode);

        // Assert
        assertTrue(result.get("success").asBoolean());
    }

    @Test
    public void getCommodityById_witNonExistingCommodity_shouldGetCommodityNotFound() {
        // Arrange
        ObjectNode idNode = mapper.createObjectNode();
        idNode.put("id", 100);

        // Act
        JsonNode result = cps.getCommodityById(idNode);

        // Assert
        assertFalse(result.get("success").asBoolean());
    }

    // get commodities by category tests
    @Test
    public void getCommoditiesByCategory_withValidInputs_shouldGetCommodities() {
        // Arrange
        ArrayList<Commodity> expected = new ArrayList<>();
        expected.add(commodities[0]);
        expected.add(commodities[1]);
        ObjectNode categoryNode = mapper.createObjectNode();
        categoryNode.put("category", "Vegetables");

        // Act
        JsonNode actual = cps.getCommoditiesByCategory(categoryNode).get("data");
        int actualSize = actual.get("CommoditiesListByCategory").size();

        // Assert
        assertEquals(expected.size(), actualSize);
    }

    // add commodity to buyList tests
    @Test
    public void addToBuyList_withValidInputs_shouldAddCommodityToUserBuyList() throws Exception {
        // Arrange
        String username = users[0].getUsername();
        Integer commodityId = commodities[0].getId();
        ObjectNode buyListNode = JsonNodeFactory.instance.objectNode();
        buyListNode.put("username", username);
        buyListNode.put("commodityId", commodityId);

        // Act
        cps.addToBuyList(buyListNode);

        // Assert
        assertEquals(1, cps.findUser(username).getBuyList().size());
        assertEquals(commodityId.intValue(), cps.findUser(username).getBuyList().get(0).get("id").asInt());
        assertEquals(commodities[0].getInStock() - 1, cps.findCommodity(commodityId).getInStock());
    }

    @Test
    public void addToBuyList_withNonExistingUser_shouldGetUserNotFound() {
        // Arrange
        String username = "amir_fery";
        Integer commodityId = commodities[0].getId();
        ObjectNode buyListNode = JsonNodeFactory.instance.objectNode();
        buyListNode.put("username", username);
        buyListNode.put("commodityId", commodityId);

        // Build expected response (exception message)
        String errorMsg = "No user with username " + username + " found";
        JsonNode responseText = new TextNode(errorMsg);
        ObjectNode response = mapper.createObjectNode();
        response.set("response", responseText);
        JsonNode expectedResponse = cps.createJsonResult(false, response);

        // Act & Assert
        assertEquals(expectedResponse.toString(), cps.addToBuyList(buyListNode).toString());
    }

    @Test
    public void addToBuyList_withNonExistingCommodity_shouldGetCommodityNotFound() {
        // Arrange
        String username = users[0].getUsername();
        Integer commodityId = 443;
        ObjectNode buyListNode = JsonNodeFactory.instance.objectNode();
        buyListNode.put("username", username);
        buyListNode.put("commodityId", commodityId);

        // Build expected response (exception message)
        String errorMsg = "Commodity with id " + commodityId + " not found";
        JsonNode responseText = new TextNode(errorMsg);
        ObjectNode response = mapper.createObjectNode();
        response.set("response", responseText);
        JsonNode expectedResponse = cps.createJsonResult(false, response);

        // Act & Assert
        assertEquals(expectedResponse.toString(), cps.addToBuyList(buyListNode).toString());
    }

    @Test
    public void addToBuyList_withOutOfStockCommodity_shouldGetCommodityOutOfStock() {
        // Arrange
        String username = users[0].getUsername();
        Integer commodityId = commodities[2].getId();
        ObjectNode buyListNode = JsonNodeFactory.instance.objectNode();
        buyListNode.put("username", username);
        buyListNode.put("commodityId", commodityId);

        // Build expected response (exception message)
        String errorMsg = "Commodity with id " + commodityId + " is out of stock";
        JsonNode responseText = new TextNode(errorMsg);
        ObjectNode response = mapper.createObjectNode();
        response.set("response", responseText);
        JsonNode expectedResponse = cps.createJsonResult(false, response);

        // Act & Assert
        assertEquals(expectedResponse.toString(), cps.addToBuyList(buyListNode).toString());
    }

    @After
    public void tearDown() {
        cps = null;
    }
}
