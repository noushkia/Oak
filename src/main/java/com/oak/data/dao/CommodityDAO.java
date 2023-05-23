package com.oak.data.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oak.data.ConnectionPool;
import com.oak.domain.Commodity;
import com.oak.domain.User;
import com.oak.exception.Provider.ProviderNotFound;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class CommodityDAO {
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
                if (e.getSQLState().equals("23503")) {
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
}
