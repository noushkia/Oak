package com.oak.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oak.data.dao.DAOLayer;
import com.oak.exception.User.UserNotFound;
import com.oak.application.service.UserService;
import com.oak.data.Database;
import com.oak.domain.Commodity;
import com.oak.domain.Provider;
import com.oak.domain.User;
import com.oak.exception.Commodity.CommodityInBuyList;
import com.oak.exception.Commodity.CommodityNotFound;
import com.oak.exception.Commodity.CommodityOutOfStock;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class UserServiceTest {
    private static Database database;
    private static DAOLayer daoLayer;
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

        userService = new UserService(database, daoLayer);
    }

    // add commodity to buyList tests
    @Test
    public void addToBuyList_withValidInputs_shouldAddCommodityToUserBuyList() throws CommodityOutOfStock, UserNotFound, CommodityInBuyList, CommodityNotFound {
        // Arrange
        String username = users[0].getUsername();
        Integer commodityId = commodities[0].getId();
        int initialBuyListSize = userService.getUserById(username).getBuyListCommodities().size();

        // Act
        userService.addToBuyList(username, commodityId);

        // Assert
        assertEquals("Commodity not added to buyList", 1, userService.getUserById(username).getBuyListCommodities().size()-initialBuyListSize);
        assertEquals("Wrong commodity was added to buyList", commodityId, userService.getUserById(username).getBuyListCommodities().get(0).getId());
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

    @Test
    public void getBuyList_withValidUserAndEmptyBuyList_shouldReturnEmptyList() throws UserNotFound {
        // Arrange
        String username = users[1].getUsername();
        List<Commodity> expected = userService.getUserById(username).getBuyListCommodities();

        // Act
        List<Commodity> actual = userService.getBuyList(username);

        // Assert
        assertEquals("Returned buyList is not valid", expected, actual);
    }

    @Test
    public void getBuyList_withUserNotFound_shouldGetUserNotFound() {
        // Arrange
        String username = "amirfery";

        // Act & Assert
        assertThrows(UserNotFound.class, () -> userService.getBuyList(username));
    }

    @Test
    public void getBuyList_withValidUserAndMultipleCommodities_shouldReturnValidList() throws UserNotFound, CommodityOutOfStock, CommodityInBuyList, CommodityNotFound {
        // Arrange
        String username = users[0].getUsername();
        List<Commodity> expected = userService.getUserById(username).getBuyListCommodities();
        expected.add(commodities[1]);
        expected.add(commodities[3]);

        userService.addToBuyList(username, commodities[1].getId());
        userService.addToBuyList(username, commodities[3].getId());

        // Act
        List<Commodity> actual = userService.getBuyList(username);

        // Assert
        assertEquals("Returned buyList is not valid", expected, actual);
    }

    @Test
    public void getBuyList_withMultipleUsers_shouldReturnValidList() throws UserNotFound, CommodityOutOfStock, CommodityInBuyList, CommodityNotFound {
        // Arrange
        String username1 = users[0].getUsername();
        String username2 = users[1].getUsername();
        List<Commodity> expected = userService.getUserById(username2).getBuyListCommodities();
        expected.add(commodities[1]);

        userService.addToBuyList(username2, commodities[1].getId());

        // Act
        List<Commodity> actual = userService.getBuyList(username1);

        // Assert
        assertNotEquals("Returned buyList is not valid", expected, actual);
    }


    @After
    public void tearDown() {
        userService = null;
        database = null;
    }

    @AfterClass
    public static void finalTearDown() {
        users = null;
        providers = null;
        commodities = null;
    }
}
