package com.oak.data.dao;

import com.oak.data.ConnectionPool;
import com.oak.domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

    private void fillUserStatement(PreparedStatement userStatement, User user) throws SQLException {
        userStatement.setString(1, user.getUsername());
        userStatement.setString(2, user.getPassword());
        userStatement.setString(3, user.getEmail());
        userStatement.setDate(4, new java.sql.Date(user.getBirthDate().getTime()));
        userStatement.setString(5, user.getAddress());
        userStatement.setInt(6, user.getCredit());
    }

    public void addUser(User user) {
        try {
            Connection con = ConnectionPool.getConnection();
            con.setAutoCommit(false);
            PreparedStatement userStatement = con.prepareStatement(
                    "INSERT INTO User(username, password, email, birthDate, address, credit) "
                            + " VALUES(?,?,?,?,?,?)"
                            + "ON DUPLICATE KEY UPDATE "
                            + "password = VALUES(password), "
                            + "email = VALUES(email), "
                            + "birthDate = VALUES(birthDate), "
                            + "address = VALUES(address);"
            );
            fillUserStatement(userStatement, user);
            try {
                userStatement.execute();
                con.commit();
            } catch (Exception e) {
                con.rollback();
            } finally {
                userStatement.close();
                con.close();
            }
        } catch (SQLException ignored) {}
    }
}
