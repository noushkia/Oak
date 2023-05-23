package com.oak.data.dao;

import com.oak.data.ConnectionPool;
import com.oak.domain.Discount;
import com.oak.exception.Discount.DiscountNotFound;
import com.oak.exception.Discount.ExpiredDiscount;
import com.oak.exception.User.UserNotFound;

import java.sql.*;

public class DiscountDAO {
    public DiscountDAO() throws SQLException {
        Connection con = ConnectionPool.getConnection();
        Statement createTableStatement = con.createStatement();
        con.setAutoCommit(false);
        createTableStatement.addBatch(
                "CREATE TABLE IF NOT EXISTS Discount(code VARCHAR(50), discount INT, " +
                        "PRIMARY KEY(code));"
        );
        createTableStatement.executeBatch();
        con.commit();
        createTableStatement.close();
        con.close();
    }

    private void fillDiscountStatement(PreparedStatement discountStatement, Discount discount) throws SQLException {
        discountStatement.setString(1, discount.getCode());
        discountStatement.setInt(2, discount.getDiscount());
    }

    public void addDiscount(Discount discount) {
        try {
            Connection con = ConnectionPool.getConnection();
            con.setAutoCommit(false);
            PreparedStatement discountStatement = con.prepareStatement(
                    "INSERT INTO Discount(code, discount) "
                            + " VALUES(?,?)"
                            + "ON DUPLICATE KEY UPDATE "
                            + "discount = VALUES(discount);"
            );
            fillDiscountStatement(discountStatement, discount);
            try {
                discountStatement.execute();
                con.commit();
            } catch (Exception e) {
                con.rollback();
            } finally {
                discountStatement.close();
                con.close();
            }
        } catch (SQLException ignored) {
        }
    }

    public void addUsedDiscount(String username, String discountCode) throws ExpiredDiscount, UserNotFound, DiscountNotFound {
        try {
            Connection connection = ConnectionPool.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement usedDiscountStatement = connection.prepareStatement(
                    "INSERT INTO UsedDiscount(username, discountCode) " +
                            "VALUES (?, ?);"
            );
            usedDiscountStatement.setString(1, username);
            usedDiscountStatement.setString(2, discountCode);
            try {
                usedDiscountStatement.executeUpdate();
                connection.commit();
                usedDiscountStatement.close();
                connection.close();
            } catch (SQLException sqlException) {
                connection.rollback();
                usedDiscountStatement.close();
                connection.close();
                if (sqlException.getSQLState().equals("23000")) {
                    String errorMessage = sqlException.getMessage();

                    if (errorMessage.contains("Duplicate entry")) {
                        throw new ExpiredDiscount(discountCode);
                    } else if (errorMessage.contains("User")) {
                        throw new UserNotFound(username);
                    } else if (errorMessage.contains("Discount")) {
                        throw new DiscountNotFound(discountCode);
                    }
                }
            }
        } catch (SQLException ignored) {
        }
    }
}
