package org.ie.tk.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ie.tk.application.service.UserService;
import org.ie.tk.data.Database;
import org.ie.tk.domain.Commodity;
import org.ie.tk.domain.Provider;
import org.ie.tk.domain.User;
import org.ie.tk.exception.Commodity.CommodityInBuyList;
import org.ie.tk.exception.Commodity.CommodityNotFound;
import org.ie.tk.exception.Commodity.CommodityOutOfStock;
import org.ie.tk.exception.User.UserNotFound;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class UserServiceTest {
    private static Database database;
    private static UserService userService;
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
        for (User user : users) {
            database.addUser(user);
        }
        for (Provider provider: providers) {
            database.addProvider(provider);
        }
        for (Commodity commodity : commodities) {
            database.addCommodity(commodity);
        }

        userService = new UserService(database);
    }

    // add commodity to buyList tests
    @Test
    public void addToBuyList_withValidInputs_shouldAddCommodityToUserBuyList() throws CommodityOutOfStock, UserNotFound, CommodityInBuyList, CommodityNotFound {
        // Arrange
        String username = users[0].getUsername();
        Integer commodityId = commodities[0].getId();

        // Act
        userService.addToBuyList(username, commodityId);

        // Assert
        assertEquals("Commodity not added to buyList", 1, userService.getUserById(username).getBuyList().size());
        assertEquals("Wrong commodity was added to buyList", commodityId, userService.getUserById(username).getBuyList().get(0).getId());
    }

    @Test
    public void addToBuyList_withNonExistingUser_shouldGetUserNotFound() {
        // Arrange
        String username = "amirfery";
        Integer commodityId = commodities[0].getId();

        // Act & Assert
        assertThrows(UserNotFound.class, () -> userService.addToBuyList(username, commodityId));
    }

    @Test
    public void addToBuyList_withNonExistingCommodity_shouldGetCommodityNotFound() {
        // Arrange
        String username = users[0].getUsername();
        Integer commodityId = 443;

        // Act & Assert
        assertThrows(CommodityNotFound.class, () -> userService.addToBuyList(username, commodityId));
    }

    @Test
    public void addToBuyList_withOutOfStockCommodity_shouldGetCommodityOutOfStock() {
        // Arrange
        String username = users[0].getUsername();
        Integer commodityId = commodities[2].getId();

        // Act & Assert
        assertThrows(CommodityOutOfStock.class, () -> userService.addToBuyList(username, commodityId));
    }

    @AfterClass
    public static void tearDown() {
        userService = null;
        database = null;
        users = null;
        providers = null;
        commodities = null;
    }
}
