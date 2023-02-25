package org.ie.tk;

import static org.junit.Assert.*;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.ie.tk.Exception.Commodity.CommodityNotFound;
import org.ie.tk.Exception.User.UserNotFound;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


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

}
