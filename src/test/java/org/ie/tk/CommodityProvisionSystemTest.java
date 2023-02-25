package org.ie.tk;

import static org.junit.Assert.*;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
    public void testRateCommodity() throws Exception {
        // Arrange
        String username = users[0].getUsername();
        String commodityId = commodities[0].getId();
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

    // getCommodityById tests
    @Test
    public void testGetCommodityByIdSuccess() throws Exception {
        // Arrange
        String id = commodities[0].getId();
        ObjectNode idNode = mapper.createObjectNode();
        idNode.put("id", id);
        JsonNode expectedData = mapper.valueToTree(cps.findCommodity(String.valueOf(idNode.get("id").textValue()))).get("objectNode");

        // Act
        JsonNode result = cps.getCommodityById(idNode);

        // Assert
        assertTrue(result.get("success").asBoolean());
    }

    @Test
    public void testGetCommodityByIdCommodityNotFound() {
        // Arrange
        ObjectNode idNode = mapper.createObjectNode();
        idNode.put("id", "456");

        // Act
        JsonNode result = cps.getCommodityById(idNode);

        // Assert
        assertFalse(result.get("success").asBoolean());
    }

    // get commodities by category tests

    @Test
    public void testGetCommoditiesByCategory() {
        // Arrange
        ArrayList<Commodity> expected = new ArrayList<>();
        //todo: how to check if commodities have the same category, adn tey are Vegetables?
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
        String commodityId = commodities[0].getId();
        ObjectNode buyListNode = JsonNodeFactory.instance.objectNode();
        buyListNode.put("username", username);
        buyListNode.put("commodityId", commodityId);

        // Act
        cps.addToBuyList(buyListNode);

        // Assert
        assertEquals(1, cps.findUser(username).getBuyList().size());
        assertEquals(commodityId, String.valueOf(cps.findUser(username).getBuyList().get(0).get("id").textValue()));
        assertEquals(commodities[0].getInStock() - 1, cps.findCommodity(commodityId).getInStock());
    }

    @Test
    public void addToBuyList_withNonExistingUser_shouldGetUserNotFound() {
        // Arrange
        String username = "amir_fery";
        String commodityId = commodities[0].getId();
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
        String commodityId = "443";
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
}
