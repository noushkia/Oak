package com.oak.data.dao;

import com.oak.data.ConnectionPool;
import com.oak.domain.Discount;
import com.oak.domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

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
        } catch (SQLException ignored) {}
    }
}
