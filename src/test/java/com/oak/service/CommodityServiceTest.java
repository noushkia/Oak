package com.oak.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oak.data.dao.DAOLayer;
import com.oak.exception.Commodity.InvalidRating;
import com.oak.exception.User.UserNotFound;
import com.oak.application.service.CommodityService;
import com.oak.data.Database;
import com.oak.domain.Commodity;
import com.oak.domain.Provider;
import com.oak.domain.User;
import com.oak.exception.Commodity.CommodityNotFound;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class CommodityServiceTest {
    private static Database database;
    private static DAOLayer daoLayer;
    private static CommodityService commodityService;
    static User[] users;
    static Provider[] providers;
    static Commodity[] commodities;

    @BeforeClass
    public static void initialSetup() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        users = mapper.readValue(new File("src/test/resources/users.json"), User[].class);
        providers = mapper.readValue(new File("src/test/resources/providers.json"), Provider[].class);
        commodities = mapper.readValue(new File("src/test/resources/commodities.json"), Commodity[].class);
    }

    @Before
    public void setup() {
        database = new Database();
        daoLayer = new DAOLayer();
        for (User user : users) {
            database.addUser(user);
        }
        for (Provider provider: providers) {
            database.addProvider(provider);
        }
        for (Commodity commodity : commodities) {
            database.addCommodity(commodity);
        }

        commodityService = new CommodityService(database, daoLayer);
    }

    // rate commodity tests
    @Test
    public void rateCommodity_withValidInputs_shouldAddRating() throws Exception {
        // Arrange
        String username = users[0].getUsername();
        Integer commodityId = commodities[0].getId();
        String score = "4";

        // Act
        commodityService.rateCommodity(username, commodityId, score);

        // Verify: Get the commodity and verify the rating was added
        Commodity commodity = commodityService.getCommodityById(commodityId);
        assertTrue(commodity.getUserRatings().containsKey(username));
        assertEquals(4, commodity.getUserRatings().get(username).intValue());
    }

    @Test
    public void rateCommodity_withInvalidType_shouldGetInvalidRating() {
        // Arrange
        String username = users[0].getUsername();
        Integer commodityId = commodities[0].getId();
        String score = "4.5";

        // Act & Assert
        assertThrows(InvalidRating.class, () -> commodityService.rateCommodity(username, commodityId, score));
    }

    @Test
    public void rateCommodity_outOfBoundRating_shouldGetInvalidRating() {
        // Arrange
        String username = users[0].getUsername();
        Integer commodityId = commodities[0].getId();
        String score = "100";

        // Act & Assert
        assertThrows(InvalidRating.class, () -> commodityService.rateCommodity(username, commodityId, score));
    }

    @Test
    public void rateCommodity_duplicateRating_shouldUpdateRating() throws CommodityNotFound, InvalidRating, UserNotFound {
        // Arrange
        String username = users[0].getUsername();
        Integer commodityId = commodities[0].getId();
        String score = "3";
        commodityService.rateCommodity(username, commodityId, score);
        score = "7";

        // Act
        commodityService.rateCommodity(username, commodityId, score);

        // Verify the rating was updated
        assertEquals(commodityService.getCommodityById(commodityId).getUserRatings().get(username).intValue(), 7);
    }

    @Test
    public void rateCommodity_withNonExistingUser_shouldGetUserNotFound() {
        // Arrange
        String username = "amirfery";
        Integer commodityId = commodities[0].getId();
        String score = "5";

        // Act & Assert
        assertThrows(UserNotFound.class, () -> commodityService.rateCommodity(username, commodityId, score));
    }

    @Test
    public void rateCommodity_withNonExistingCommodity_shouldGetCommodityNotFound() {
        // Arrange
        String username = users[0].getUsername();
        Integer commodityId = 100;
        String score = "5";

        // Act & Assert
        assertThrows(CommodityNotFound.class, () -> commodityService.rateCommodity(username, commodityId, score));
    }

    // getCommodityById tests
    @Test
    public void getCommodityById_withValidInputs_shouldGetCommodity() throws CommodityNotFound {
        // Arrange
        Integer id = commodities[0].getId();

        // Act
        Commodity commodity = commodityService.getCommodityById(id);

        // Assert
        assertEquals(commodity.getId(), id);
    }

    @Test
    public void getCommodityById_witNonExistingCommodity_shouldGetCommodityNotFound() {
        // Arrange
        Integer id = 100;

        // Act & Assert
        assertThrows(CommodityNotFound.class, () -> commodityService.getCommodityById(id));
    }

    // get commodities by category tests
    @Test
    public void getCommoditiesByCategory_withValidInputs_shouldGetCommodities() {
        // Arrange
        int expectedSize = 8;
        String category = "Vegetables";

        // Act
        List<Commodity> actual = commodityService.getCommoditiesByCategory(category);
        int actualSize = actual.size();

        // Assert
        assertEquals(expectedSize, actualSize);
    }


    // get commodities by price tests
    @Test
    public void getCommoditiesByPrice_withStartPriceGreaterThanAnyCommodity_shouldReturnEmptyList() {
        // Arrange
        Integer startPrice = 100000000;
        Integer endPrice = 200000000;

        // Act
        List<Commodity> actual = commodityService.getCommoditiesByPrice(startPrice, endPrice);

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void getCommoditiesByPrice_withEndPriceLessThanAnyCommodity_shouldReturnEmptyList() {
        // Arrange
        Integer startPrice = 1;
        Integer endPrice = 4;

        // Act
        List<Commodity> actual = commodityService.getCommoditiesByPrice(startPrice, endPrice);

        // Assert
        assertTrue(actual.isEmpty());
    }

    @Test
    public void getCommoditiesByPrice_withOnlyOneMatchingCommodity_shouldReturnOneCommodity() {
        // Arrange
        Integer startPrice = 10000;
        Integer endPrice = 10000;

        // Act
        List<Commodity> actual = commodityService.getCommoditiesByPrice(startPrice, endPrice);

        // Assert
        assertEquals(1, actual.size());
        assertTrue(actual.get(0).getName().contains("Onion"));
    }

    @Test
    public void getCommoditiesByPrice_withAllCommoditiesInRange_shouldReturnAllCommodities() {
        // Arrange
        Integer startPrice = 1;
        Integer endPrice = 100000000;

        // Act
        List<Commodity> actual = commodityService.getCommoditiesByPrice(startPrice, endPrice);

        // Assert
        assertEquals(commodities.length, actual.size());
    }


    @After
    public void tearDown() {
        commodityService = null;
        database = null;
    }

    @AfterClass
    public static void finalTearDown() {
        users = null;
        providers = null;
        commodities = null;
    }
}
