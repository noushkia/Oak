package com.oak.data.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oak.data.ConnectionPool;
import com.oak.domain.Commodity;
import com.oak.exception.Provider.ProviderNotFound;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommodityDAO {
    private String baseQuery = "SELECT * FROM Commodity";
    private String currentQuery = baseQuery;
    private String sort = null;
    private ArrayList<String> conditions = new ArrayList<>();
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

    public void addAvailableCondition() {
        conditions.add(
                "inStock > 0"
        );
    }

    public void addSort(String attribute) {
        sort = "ORDER BY " + attribute + " ASC";
    }

    public void addProviderCondition(String providerName) {
        currentQuery = "WITH ProviderId AS (" +
                    "SELECT id" +
                    "FROM Provider" +
                    "WHERE name LIKE name" +
                    ")" +
                    "SELECT * FROM Commodity c" +
                    "INNER JOIN ProviderId p ON c.providerId = p.id"
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
                            + " VALUES(?,?,?,?,?,?,?,?)"
                            + "ON DUPLICATE KEY UPDATE "
                            + "name = VALUES(name), "
                            + "providerId = VALUES(providerId), "
                            + "price = VALUES(price), "
                            + "categories = VALUES(categories),"
                            + "rating = VALUES(rating),"
                            + "inStock = VALUES(inStock),"
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
        } catch (SQLException | JsonProcessingException ignored) {
        }
    }

    private Commodity createCommodity(ResultSet result) throws SQLException, JsonProcessingException {
        int id = result.getInt("id");
        String name = result.getString("name");
        Integer providerId = result.getInt("providerId");
        Integer price = result.getInt("price");

        String categoriesJson = result.getString("categories");
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<String> categories = objectMapper.readValue(categoriesJson, new TypeReference<>(){});

        Double rating = result.getDouble("rating");
        Integer inStock = result.getInt("inStock");
        String image = result.getString("image");
        return new Commodity(id, name, providerId, price,
                categories, rating, inStock, image);
    }

    public List<Commodity> fetchCommodities(Integer providerId) {
        try {
            Connection con = ConnectionPool.getConnection();
            con.setAutoCommit(false);
            PreparedStatement getProviderCommoditiesStatement = con.prepareStatement(
                    "SELECT * FROM Commodity " +
                            "WHERE providerId = ?;"
            );
            getProviderCommoditiesStatement.setInt(1, providerId);
            try {
                ResultSet result = getProviderCommoditiesStatement.executeQuery();
                ArrayList<Commodity> commodities = new ArrayList<>();
                while (result.next()) {
                    commodities.add(createCommodity(result));
                }
                getProviderCommoditiesStatement.close();
                con.close();
                return commodities;
            } catch (SQLException | JsonProcessingException e) {
                con.rollback();
                getProviderCommoditiesStatement.close();
                con.close();
            }
        } catch (SQLException ignored) {}
        return null;
    }
}
