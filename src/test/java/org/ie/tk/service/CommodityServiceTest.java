package org.ie.tk.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ie.tk.application.service.CommodityService;
import org.ie.tk.data.Database;
import org.ie.tk.domain.Commodity;
import org.ie.tk.domain.Provider;
import org.ie.tk.domain.User;
import org.ie.tk.exception.Commodity.CommodityNotFound;
import org.ie.tk.exception.Commodity.InvalidRating;
import org.ie.tk.exception.User.UserNotFound;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CommodityServiceTest {
    private static Database database;
    private static CommodityService commodityService;
    static User[] users;
    static Provider[] providers;
    static Commodity[] commodities;

    @BeforeClass
    public static void initialSetup() throws IOException {
        // todo: should we initialize database for each test?
        ObjectMapper mapper = new ObjectMapper();
        users = mapper.readValue(new File("src/test/resources/users.json"), User[].class);
        providers = mapper.readValue(new File("src/test/resources/providers.json"), Provider[].class);
        commodities = mapper.readValue(new File("src/test/resources/commodities.json"), Commodity[].class);

        database = new Database();
        for (User user : users) {
            database.addUser(user);
        }
        for (Provider provider: providers) {
            database.addProvider(provider);
        }
        for (Commodity commodity : commodities) {
            database.addCommodity(commodity);
        }

        commodityService = new CommodityService(database);
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
        String username = users[0].getUsername();
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
        Integer id = commodities[0].getId();

        // Act & Assert
        assertThrows(CommodityNotFound.class, () -> commodityService.getCommodityById(id));
    }

    // get commodities by category tests
    @Test
    public void getCommoditiesByCategory_withValidInputs_shouldGetCommodities() {
        // Arrange
        ArrayList<Commodity> expected = new ArrayList<>();
        expected.add(commodities[0]);
        expected.add(commodities[1]);
        String category = "Vegetables";

        // Act
        List<Commodity> actual = commodityService.getCommoditiesByCategory(category);
        int actualSize = actual.size();

        // Assert
        assertEquals(expected.size(), actualSize);
    }


    @AfterClass
    public static void tearDown() {
        commodityService = null;
        database = null;
        users = null;
        providers = null;
        commodities = null;
    }
}
