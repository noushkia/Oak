package com.oak.data.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oak.data.ConnectionPool;
import com.oak.domain.Commodity;
import com.oak.exception.Commodity.CommodityNotFound;
import com.oak.exception.Provider.ProviderNotFound;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommodityDAO {
    private final String baseQuery = "SELECT * FROM Commodity";
    private String currentQuery = baseQuery;
    private String sort = "";
    private String pagination = "";
    private final ArrayList<String> conditions = new ArrayList<>();

    public CommodityDAO() throws SQLException {
        Connection con = ConnectionPool.getConnection();
        Statement createTableStatement = con.createStatement();
        con.setAutoCommit(false);
        createTableStatement.addBatch(
                "CREATE TABLE IF NOT EXISTS Commodity(id INT, name VARCHAR(50), " +
                        "providerId INT, price INT, categories TEXT, " +
                        "rating DOUBLE, inStock INT, image VARCHAR(255)," +
                        "PRIMARY KEY(id)," +
                        "FOREIGN KEY (providerId) REFERENCES Provider(id));"
        );
        createTableStatement.executeBatch();
        con.commit();
        createTableStatement.close();
        con.close();
    }

    public void setAvailableCondition() {
        conditions.add(
                "inStock > 0"
        );
    }

    public void setCategoryCondition(String category) {
        conditions.add(
                "JSON_SEARCH(categories, 'one', '" + category + "') IS NOT NULL"
        );
    }

    public void setNameCondition(String name) {
        conditions.add(
                "name LIKE '%" + name + "%'"
        );
    }

    public void setProviderIdCondition(Integer providerId) {
        conditions.add(
                "providerId = " + providerId
        );
    }

    public void setCommodityIdCondition(Integer commodityId) {
        conditions.add(
                "id = " + commodityId
        );
    }

    public void setPriceCondition(Integer startPrice, Integer endPrice) {
        conditions.add(
                "(price >= " + startPrice + " AND price <= " + endPrice + ")"
        );
    }


    public void reset() {
        conditions.clear();
        currentQuery = baseQuery;
        sort = "";
        pagination = "";
    }

    public void setSort(String attribute) {
        sort = " ORDER BY " + attribute + " ASC";
    }

    public void setPagination(Integer limit, Integer pageNumber) {
        pagination = " LIMIT " + limit + " OFFSET " + limit * (pageNumber - 1);
    }

    public void setProviderNameCondition(String providerName) {
        currentQuery = "WITH ProviderId AS (" +
                "SELECT id " +
                "FROM Provider " +
                "WHERE name LIKE '%" + providerName +
                "%') " +
                "SELECT * FROM Commodity c " +
                "INNER JOIN ProviderId p ON c.providerId = p.id";
    }

    private void fillCommodityStatement(PreparedStatement commodityStatement, Commodity commodity) throws SQLException, JsonProcessingException {
        commodityStatement.setInt(1, commodity.getId());
        commodityStatement.setString(2, commodity.getName());
        commodityStatement.setInt(3, commodity.getProviderId());
        commodityStatement.setInt(4, commodity.getPrice());
        List<String> categories = commodity.getCategories();
        String categoriesJson = new ObjectMapper().writeValueAsString(categories);
        commodityStatement.setString(5, categoriesJson);
        commodityStatement.setDouble(6, commodity.getOriginalRating());
        commodityStatement.setInt(7, commodity.getInStock());
        commodityStatement.setString(8, commodity.getImage());
    }

    public void addCommodity(Commodity commodity) throws ProviderNotFound {
        try {
            Connection con = ConnectionPool.getConnection();
            con.setAutoCommit(false);
            PreparedStatement commodityStatement = con.prepareStatement(
                    "INSERT INTO Commodity(id, name, providerId, price, categories, rating, inStock, image) "
                            + "VALUES(?,?,?,?,?,?,?,?) "
                            + "ON DUPLICATE KEY UPDATE "
                            + "name = VALUES(name), "
                            + "providerId = VALUES(providerId), "
                            + "price = VALUES(price), "
                            + "categories = VALUES(categories),"
                            + "rating = VALUES(rating),"
                            + "image = VALUES(image);"
            );
            fillCommodityStatement(commodityStatement, commodity);
            try {
                commodityStatement.execute();
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                if (e.getSQLState().equals("23000")) {
                    commodityStatement.close();
                    con.close();
                    throw new ProviderNotFound(commodity.getProviderId());
                }
            } finally {
                commodityStatement.close();
                con.close();
            }
        } catch (SQLException | JsonProcessingException ignored) {}
    }

    public void addRating(String username, Integer commodityId, Integer rating) {
        try {
            Connection con = ConnectionPool.getConnection();
            con.setAutoCommit(false);
            PreparedStatement ratingStatement = con.prepareStatement(
                    "INSERT INTO Rating(username, commodityId, rating) "
                            + "VALUES(?,?,?) "
                            + "ON DUPLICATE KEY UPDATE "
                            + "rating = VALUES(rating);"
            );
            ratingStatement.setString(1, username);
            ratingStatement.setInt(2, commodityId);
            ratingStatement.setInt(3, rating);
            try {
                ratingStatement.execute();
                con.commit();
            } catch (SQLException e) {
                con.rollback();
            } finally {
                ratingStatement.close();
                con.close();
            }
        } catch (SQLException ignored) {}
    }

    private Commodity createCommodity(ResultSet result) throws SQLException, JsonProcessingException {
        int id = result.getInt("id");
        String name = result.getString("name");
        Integer providerId = result.getInt("providerId");
        Integer price = result.getInt("price");

        String categoriesJson = result.getString("categories");
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<String> categories = objectMapper.readValue(categoriesJson, new TypeReference<>() {
        });

        Double rating = result.getDouble("rating");
        Integer inStock = result.getInt("inStock");
        String image = result.getString("image");
        return new Commodity(id, name, providerId, price,
                categories, rating, inStock, image);
    }

    public Integer getNumberOfPages(Integer limit) {
        int numPages = 0;
        try {
            Connection con = ConnectionPool.getConnection();
            con.setAutoCommit(false);
            String condition = String.join(" AND ", conditions);
            condition = !condition.equals("") ? " WHERE " + condition : condition;
            PreparedStatement getNumberOfPagesStatement = con.prepareStatement(
                    "SELECT CEIL(COUNT(*) / ?) AS numPages "
                            + "FROM ("
                            + currentQuery
                            + condition
                            + ") AS commodities;"
            );
            getNumberOfPagesStatement.setInt(1, limit);
            try {
                ResultSet set = getNumberOfPagesStatement.executeQuery();
                if (set.next()) {
                    numPages = set.getInt("numPages");
                }
                con.commit();
            } catch (SQLException e) {
                con.rollback();
            } finally {
                getNumberOfPagesStatement.close();
                con.close();
            }
        } catch (SQLException ignored) {}
        return numPages;
    }

    public Commodity fetchCommodity(Integer commodityId) throws CommodityNotFound {
        reset();
        setCommodityIdCondition(commodityId);
        List<Commodity> commodities = fetchCommodities();
        reset();
        if (commodities.size() > 0) {
            return commodities.get(0);
        }
        else {
            throw new CommodityNotFound(commodityId);
        }
    }

    public List<Commodity> fetchCommodities() {
        ArrayList<Commodity> commodities = new ArrayList<>();
        try {
            Connection con = ConnectionPool.getConnection();
            con.setAutoCommit(false);
            Statement getCommoditiesStatement = con.createStatement();
            String condition = String.join(" AND ", conditions);
            condition = !condition.equals("") ? " WHERE " + condition : condition;
            String finalQuery = currentQuery + condition + sort + pagination + ";";
            try {
                ResultSet result = getCommoditiesStatement.executeQuery(
                        finalQuery
                );
                while (result.next()) {
                    commodities.add(createCommodity(result));
                }
            } catch (SQLException | JsonProcessingException e) {
                con.rollback();
            } finally {
                getCommoditiesStatement.close();
                con.close();
            }
        } catch (SQLException ignored) {}
        return commodities;
    }


    public List<Commodity> fetchProviderCommodities(Integer providerId) {
        reset();
        setProviderIdCondition(providerId);
        List<Commodity> commodities = fetchCommodities();
        reset();
        return commodities;
    }

    public HashMap<String, Integer> fetchRatings(Integer commodityId) {
        HashMap<String, Integer> ratings = new HashMap<>();
        try {
            Connection con = ConnectionPool.getConnection();
            con.setAutoCommit(false);
            PreparedStatement getRatingsStatement = con.prepareStatement(
                    "SELECT * FROM Rating "
                            + "WHERE commodityId = ?;"
            );
            getRatingsStatement.setInt(1, commodityId);
            try {
                ResultSet result = getRatingsStatement.executeQuery();
                while (result.next()) {
                    String username = result.getString("username");
                    Integer rating = result.getInt("rating");
                    ratings.put(username, rating);
                }
            } catch (SQLException e) {
                con.rollback();
            } finally {
                getRatingsStatement.close();
                con.close();
            }
        } catch (SQLException ignored) {}
        return ratings;
    }

    public void updateCommodityInStock(Integer commodityId, Integer inStock) {
        try {
            Connection connection = ConnectionPool.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement updateInStockStatement = connection.prepareStatement(
                    "UPDATE Commodity SET inStock = ? WHERE id = ?;"
            );

            updateInStockStatement.setInt(1, inStock);
            updateInStockStatement.setInt(2, commodityId);
            try {
                updateInStockStatement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
            } finally {
                updateInStockStatement.close();
                connection.close();
            }
        } catch (SQLException ignored) {}
    }
}
