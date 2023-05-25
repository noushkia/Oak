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
                            + "VALUES(?,?) "
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

    private void fillListDiscountStatement(PreparedStatement discountStatement, String username, String discountCode) throws SQLException {
        discountStatement.setString(1, username);
        discountStatement.setString(2, discountCode);
    }
    public void checkIfDiscountIsExpired(String username, String discountCode) throws ExpiredDiscount {
        try {
            Connection connection = ConnectionPool.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement usedDiscountStatement = connection.prepareStatement(
                    "SELECT * FROM UsedDiscount " +
                            "WHERE username = ? AND  discountCode = ?;"
            );
            fillListDiscountStatement(usedDiscountStatement, username, discountCode);
            try {
                ResultSet set = usedDiscountStatement.executeQuery();
                connection.commit();
                if(set.next()) {
                    usedDiscountStatement.close();
                    connection.close();
                    throw new ExpiredDiscount(discountCode);
                }
            } catch (SQLException sqlException) {
                connection.rollback();
            } finally {
                usedDiscountStatement.close();
                connection.close();
            }
        } catch (SQLException ignored) {}
    }

    public void addBuyListDiscount(String username, String discountCode) throws UserNotFound, DiscountNotFound, ExpiredDiscount {
        try {
            checkIfDiscountIsExpired(username, discountCode);
            Connection connection = ConnectionPool.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement usedDiscountStatement = connection.prepareStatement(
                    "INSERT INTO BuyListDiscount(username, discountCode) " +
                            "VALUES(?, ?) " +
                            "ON DUPLICATE KEY UPDATE " +
                            "discountCode = VALUES(discountCode);"
            );
            fillListDiscountStatement(usedDiscountStatement, username, discountCode);
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
                    if (errorMessage.contains("User")) {
                        throw new UserNotFound(username);
                    } else if (errorMessage.contains("Discount")) {
                        throw new DiscountNotFound(discountCode);
                    }
                }
            }
        } catch (SQLException ignored) {
        }
    }

    public Discount fetchDiscount(String username) {
        Discount discount = null;
        try {
            Connection con = ConnectionPool.getConnection();
            con.setAutoCommit(false);
            PreparedStatement getDiscountStatement = con.prepareStatement(
                    "SELECT * FROM BuyListDiscount " +
                            "INNER JOIN Discount ON discountCode = code " +
                            "WHERE username = ?;"
            );
            getDiscountStatement.setString(1, username);
            try {
                ResultSet result = getDiscountStatement.executeQuery();
                if (result.next()) {
                    String code = result.getString("code");
                    Integer discountVal = result.getInt("discount");

                    con.commit();
                    discount = new Discount(code, discountVal);
                }
            } catch (SQLException e) {
                con.rollback();
            } finally {
                getDiscountStatement.close();
                con.close();
            }
        } catch (SQLException ignored) {}
        return discount;
    }

    public void applyDiscount(String username, String code) {
        try {
            Connection con = ConnectionPool.getConnection();
            con.setAutoCommit(false);
            PreparedStatement deleteDiscountStatement = con.prepareStatement(
                    "DELETE FROM BuyListDiscount " +
                            "WHERE username = ? AND discountCode = ?;"
            );
            deleteDiscountStatement.setString(1, username);
            deleteDiscountStatement.setString(2, code);

            PreparedStatement addDiscountStatement = con.prepareStatement(
                    "INSERT INTO UsedDiscount(username, discountCode) " +
                            "VALUES(?, ?);"
            );
            addDiscountStatement.setString(1, username);
            addDiscountStatement.setString(2, code);
            try {
                deleteDiscountStatement.executeUpdate();
                addDiscountStatement.executeUpdate();
                con.commit();
            } catch (SQLException e) {
                con.rollback();
            } finally {
                deleteDiscountStatement.close();
                addDiscountStatement.close();
                con.close();
            }
        } catch (SQLException ignored) {}
    }
}

