package org.ie.tk;

import static org.junit.Assert.*;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.ie.tk.Exception.Commodity.CommodityNotFound;
import org.ie.tk.Exception.Provider.ProviderNotFound;
import org.ie.tk.Exception.User.UserNotFound;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;


public class CommodityProvisionSystemTest {
    private CommodityProvisionSystem cps;
    private ObjectMapper mapper;

    @Before
    public void setup() {
        cps = new CommodityProvisionSystem();
        mapper = new ObjectMapper();
        //todo: Setup Add a set of users, commodities and providers
    }

    // rate commodity tests
    @Test
    public void testRateCommodity() throws Exception {
        // Create a user
        ObjectNode userNode = mapper.createObjectNode();
        userNode.put("username", "taha_fakh");
        userNode.put("email", "taha_fakh@gmail.com");
        userNode.put("password", "koon");
        cps.addUser(userNode);

        // Create a provider
        ObjectNode providerNode = mapper.createObjectNode();
        providerNode.put("id", "provider_1");
        providerNode.put("name", "Provider 1");
        providerNode.put("address", "123 Main St");
        cps.addProvider(providerNode);

        // Create a commodity
        ObjectNode commodityNode = mapper.createObjectNode();
        commodityNode.put("id", "commodity_1");
        commodityNode.put("name", "Commodity 1");
        commodityNode.put("description", "This is commodity 1");
        commodityNode.put("price", 100);
        commodityNode.put("providerId", "provider_1");
        cps.addCommodity(commodityNode);

        // Rate the commodity
        ObjectNode ratingNode = mapper.createObjectNode();
        ratingNode.put("username", "taha_fakh");
        ratingNode.put("commodityId", "commodity_1");
        ratingNode.put("score", 4);
        JsonNode result = cps.rateCommodity(ratingNode);

        // Verify the rating was successful
        assertTrue(result.get("success").asBoolean());
        //todo: fix string check, should we even have it?
        assertEquals("Commodity with id commodity_1 rated by user with username taha_fakh successfully!", result.get("data").get("response").asText());

        // Get the commodity and verify the rating was added
        //todo: add getUserRatings
        Commodity commodity = cps.findCommodity("commodity_1");
        assertTrue(commodity.getUserRatings().containsKey("john_doe"));
        assertEquals(4, commodity.getUserRatings().get("john_doe").intValue());
    }

    // getCommodityById tests

    @Test
    public void testGetCommodityByIdSuccess() throws Exception {
        // Arrange
        ObjectNode idNode = mapper.createObjectNode();
        idNode.put("id", "456");//todo: get a setup commodity id
        JsonNode expectedData = mapper.valueToTree(cps.findCommodity(String.valueOf(idNode.get("id"))));

        // Act
        JsonNode result = cps.getCommodityById(idNode);

        // Assert
        assertTrue(result.get("success").asBoolean());
        assertEquals(expectedData, result.get("data"));
    }

    @Test
    public void testGetCommodityByIdCommodityNotFound() throws Exception {
        // Arrange
        ObjectNode idNode = mapper.createObjectNode();
        idNode.put("id", "456");

        // Act
        JsonNode result = cps.getCommodityById(idNode);

        // Assert
        assertFalse(result.get("success").asBoolean());
        assertEquals("Commodity not found with id " + idNode.get("id"), result.get("error").asText());
    }

    // get commodities by category tests

    @Test
    public void testGetCommoditiesByCategory() throws ProviderNotFound, UserNotFound, CommodityNotFound {
        // Arrange
        ArrayList<Commodity> expected = new ArrayList<>();
        //todo: use setup
        expected.add(c1);
        expected.add(c2);
        //todo: use setup
        ObjectNode categoryNode = mapper.createObjectNode();
        categoryNode.put("category", "Vegetables");

        // Act
        JsonNode actual = cps.getCommoditiesByCategory(categoryNode);
        //todo: convert JsonNode to arraylist?

        // Assert
        assertEquals(expected, actual);
    }

    // add commodity to buyList tests

    @Test
    public void addToBuyList_withValidInputs_shouldAddCommodityToUserBuyList() throws Exception {
        // Arrange
        //todo: use setup
        String username = "user1";
        String commodityId = "commodity1";
        int initialStock = 10;
        ObjectNode userNode = JsonNodeFactory.instance.objectNode();
        userNode.put("username", username);
        cps.addUser(userNode);
        ObjectNode commodityNode = JsonNodeFactory.instance.objectNode();
        commodityNode.put("id", commodityId);
        commodityNode.put("providerId", "provider1");
        commodityNode.put("name", "Commodity 1");
        commodityNode.put("description", "Description of Commodity 1");
        commodityNode.put("price", 100);
        commodityNode.put("stock", initialStock);
        cps.addProvider(JsonNodeFactory.instance.objectNode().put("id", "provider1"));
        cps.addCommodity(commodityNode);
        //todo: use setup
        ObjectNode buyListNode = JsonNodeFactory.instance.objectNode();
        buyListNode.put("username", username);
        buyListNode.put("commodityId", commodityId);

        // Act
        cps.addToBuyList(buyListNode);

        // Assert
        assertEquals(1, cps.findUser(username).getBuyList().size());
        assertEquals(commodityId, String.valueOf(cps.findUser(username).getBuyList().get(0).get("id")));
        //todo: implement getInStock
        assertEquals(initialStock - 1, cps.findCommodity(commodityId).getInStock());
    }

    @Test
    public void addToBuyList_withNonExistingUser_shouldThrowUserNotFound() throws Exception {
        // Arrange
        //todo: use setup
        String username = "user1";
        String commodityId = "commodity1";
        //todo: use setup
        ObjectNode buyListNode = JsonNodeFactory.instance.objectNode();
        buyListNode.put("username", username);
        buyListNode.put("commodityId", commodityId);

        // Act & Assert
        UserNotFound exception = assertThrows(UserNotFound.class, () -> {
            cps.addToBuyList(buyListNode);
        });
        assertEquals("User with username " + username + " not found", exception.getMessage());
    }

    @Test
    public void addToBuyList_withNonExistingCommodity_shouldThrowCommodityNotFound() throws Exception {
        // Arrange
        //todo: use setup
        String username = "user1";
        String commodityId = "commodity1";
        ObjectNode userNode = JsonNodeFactory.instance.objectNode();
        userNode.put("username", username);
        cps.addUser(userNode);
        //todo: use setup
        ObjectNode buyListNode = JsonNodeFactory.instance.objectNode();
        buyListNode.put("username", username);
        buyListNode.put("commodityId", commodityId);

        // Act & Assert
        CommodityNotFound exception = assertThrows(CommodityNotFound.class, () -> {
            cps.addToBuyList(buyListNode);
        });
        assertEquals("Commodity with id " + commodityId + " not found", exception.getMessage());
    }
}
