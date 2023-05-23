package com.oak.data.dao;

import com.oak.data.ConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDAO {
    public UserDAO() throws SQLException {
        Connection con = ConnectionPool.getConnection();
        Statement createTableStatement = con.createStatement();
        con.setAutoCommit(false);
        createTableStatement.addBatch(
                "CREATE TABLE IF NOT EXISTS User(username VARCHAR(50), password VARCHAR(50), " +
                        "email VARCHAR(50), birthDate DATETIME, address VARCHAR(255)," +
                        "credit INT, "+
                        "PRIMARY KEY(id));"
        );
        createTableStatement.addBatch(
                "CREATE TABLE IF NOT EXISTS BuyList(username VARCHAR(50), commodityId INT, " +
                        "count INT," +
                        "PRIMARY KEY(username, commodityId)," +
                        "FOREIGN KEY (username) REFERENCES User(username)," +
                        "FOREIGN KEY (commodityId) REFERENCES Commodity(id));"
        );
        createTableStatement.addBatch(
                "CREATE TABLE IF NOT EXISTS PurchasedList(username VARCHAR(50), commodityId INT, " +
                        "count INT," +
                        "PRIMARY KEY(username, commodityId)," +
                        "FOREIGN KEY (username) REFERENCES User(username)," +
                        "FOREIGN KEY (commodityId) REFERENCES Commodity(id));"
        );
        createTableStatement.addBatch(
                "CREATE TABLE IF NOT EXISTS Vote(username VARCHAR(50), commentId INT, " +
                        "vote INT," +
                        "PRIMARY KEY(username, commentId)," +
                        "FOREIGN KEY (username) REFERENCES User(username)," +
                        "FOREIGN KEY (commentId) REFERENCES Comment(id));"
        );
        createTableStatement.addBatch(
                "CREATE TABLE IF NOT EXISTS UsedDiscount(username VARCHAR(50), discountCode VARCHAR(50), " +
                        "PRIMARY KEY(username, discountCode)," +
                        "FOREIGN KEY (username) REFERENCES User(username)," +
                        "FOREIGN KEY (discountCode) REFERENCES Discount(code));"
        );
        createTableStatement.addBatch(
                "CREATE TABLE IF NOT EXISTS Rating(username VARCHAR(50), commodityId INT, " +
                        "rating INT," +
                        "PRIMARY KEY(username, commodityId)," +
                        "FOREIGN KEY (username) REFERENCES User(username)," +
                        "FOREIGN KEY (commodityId) REFERENCES Commodity(commodityId));"
        );
        createTableStatement.executeBatch();
        con.commit();
        createTableStatement.close();
        con.close();
    }
}
