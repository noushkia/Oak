package com.oak.data.dao;

import com.oak.data.ConnectionPool;
import com.oak.domain.*;
import com.oak.exception.Commodity.CommodityNotFound;
import com.oak.exception.User.UserNotFound;

import java.sql.*;

public class UserDAO {
    public UserDAO() throws SQLException {
        Connection con = ConnectionPool.getConnection();
        Statement createTableStatement = con.createStatement();
        con.setAutoCommit(false);
        createTableStatement.addBatch(
                "CREATE TABLE IF NOT EXISTS User(username VARCHAR(50), password VARCHAR(50), " +
                        "email VARCHAR(50), birthDate DATETIME, address VARCHAR(255)," +
                        "credit INT, " +
                        "PRIMARY KEY(username));"
        );
        createTableStatement.addBatch(
                "CREATE TABLE IF NOT EXISTS BuyList(username VARCHAR(50), commodityId INT, count INT," +
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
                        "vote INT, " +
                        "PRIMARY KEY(username, commentId), " +
                        "FOREIGN KEY (username) REFERENCES User(username), " +
                        "FOREIGN KEY (commentId) REFERENCES Comment(id));"
        );
        createTableStatement.addBatch(
                "CREATE TABLE IF NOT EXISTS UsedDiscount(username VARCHAR(50), discountCode VARCHAR(50), " +
                        "PRIMARY KEY(username, discountCode), " +
                        "FOREIGN KEY (username) REFERENCES User(username), " +
                        "FOREIGN KEY (discountCode) REFERENCES Discount(code));"
        );
        createTableStatement.addBatch(
                "CREATE TABLE IF NOT EXISTS BuyListDiscount(username VARCHAR(50), discountCode VARCHAR(50), " +
                        "PRIMARY KEY(username), " +
                        "FOREIGN KEY (username) REFERENCES User(username), " +
                        "FOREIGN KEY (discountCode) REFERENCES Discount(code));"
        );
        createTableStatement.addBatch(
                "CREATE TABLE IF NOT EXISTS Rating(username VARCHAR(50), commodityId INT, " +
                        "rating INT, " +
                        "PRIMARY KEY(username, commodityId), " +
                        "FOREIGN KEY (username) REFERENCES User(username), " +
                        "FOREIGN KEY (commodityId) REFERENCES Commodity(id));"
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
        } catch (SQLException ignored) {
        }
    }

    private User createUser(ResultSet result) throws SQLException {
        String username = result.getString("username");
        String password = result.getString("password");
        String email = result.getString("email");
        Date birthDate = result.getDate("birthDate");
        String address = result.getString("address");
        Integer credit = result.getInt("credit");
        return new User(username, password, email, birthDate, address, credit);
    }

    public User fetchUser(String username) throws UserNotFound {
        User user = null;
        try {
            Connection connection = ConnectionPool.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement getUserStatement = connection.prepareStatement(
                    "SELECT * FROM User " +
                            "WHERE username = ?;"
            );
            getUserStatement.setString(1, username);
            try {
                ResultSet result = getUserStatement.executeQuery();
                if (!result.next()) {
                    getUserStatement.close();
                    connection.close();
                    throw new UserNotFound(username);
                }
                connection.commit();
                user = createUser(result);
            } catch (SQLException e) {
                connection.rollback();
            } finally {
                getUserStatement.close();
                connection.close();
            }
        } catch (SQLException ignored) {
        }
        return user;
    }

    public CommodityList fetchUserList(String username, String listType, CommodityDAO commodityDAO) {
        CommodityList list = new CommodityList();
        try {
            Connection connection = ConnectionPool.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement getUserBuyListStatement = connection.prepareStatement(
                    "SELECT commodityId, count FROM " + listType +
                            " WHERE username = ?;"
            );

            getUserBuyListStatement.setString(1, username);
            try {
                ResultSet result = getUserBuyListStatement.executeQuery();
                while (result.next()) {
                    int commodityId = result.getInt("commodityId");
                    int count = result.getInt("count");
                    Commodity commodity = commodityDAO.fetchCommodity(commodityId);
                    list.getItems().put(commodityId, commodity);
                    list.getItemsCount().put(commodityId, count);
                }

                getUserBuyListStatement.close();
                connection.close();
            } catch (SQLException e) {
                connection.rollback();
                getUserBuyListStatement.close();
                connection.close();
            } catch (CommodityNotFound ignored) {
            }
        } catch (SQLException ignored) {
        }
        return list;
    }

    public void updateUserCredit(String username, Integer credit) {
        try {
            Connection connection = ConnectionPool.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement updateCreditStatement = connection.prepareStatement(
                    "UPDATE User SET credit = ? WHERE username = ?;"
            );

            updateCreditStatement.setInt(1, credit);
            updateCreditStatement.setString(2, username);
            try {
                updateCreditStatement.executeUpdate();
                connection.commit();

                updateCreditStatement.close();
                connection.close();
            } catch (SQLException e) {
                connection.rollback();
                updateCreditStatement.close();
                connection.close();
            }
        } catch (SQLException ignored) {
        }
    }

    public void updateUserBuyList(String username, Integer commodityId, Integer quantity) {
        try {
            Connection connection = ConnectionPool.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement updateStatement = connection.prepareStatement(
                    "INSERT INTO BuyList (username, commodityId, count) " +
                            "VALUES (?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE count = VALUES(count)"
            );
            updateStatement.setString(1, username);
            updateStatement.setInt(2, commodityId);
            updateStatement.setInt(3, quantity);

            Statement deleteStatement = connection.createStatement();

            try {
                updateStatement.executeUpdate();
                deleteStatement.executeUpdate(
                  "DELETE FROM BuyList " +
                          "WHERE count = 0;"
                );
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
            } finally {
                updateStatement.close();
                deleteStatement.close();
                connection.close();
            }

        } catch (SQLException ignored) {}
    }

    public void finalizeBuyList(String username) {
        try {
            Connection connection = ConnectionPool.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement moveStatement;
            PreparedStatement updateStatement;
            PreparedStatement deleteStatement;

            // Move rows from BuyList to PurchasedList
            String moveQuery = "INSERT INTO PurchasedList (username, commodityId, count) " +
                    "SELECT username, commodityId, count " +
                    "FROM BuyList " +
                    "WHERE username = ?";
            moveStatement = connection.prepareStatement(moveQuery);
            moveStatement.setString(1, username);

            // Update Commodities inStock column
            String updateQuery = "UPDATE Commodity c " +
                    "JOIN BuyList bl ON c.id = bl.commodityId " +
                    "SET c.inStock = c.inStock - bl.count " +
                    "WHERE bl.username = ?";
            updateStatement = connection.prepareStatement(updateQuery);
            updateStatement.setString(1, username);

            // Delete rows from BuyList
            String deleteQuery = "DELETE FROM BuyList WHERE username = ?";
            deleteStatement = connection.prepareStatement(deleteQuery);
            deleteStatement.setString(1, username);

            try {
                moveStatement.executeUpdate();
                updateStatement.executeUpdate();
                deleteStatement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                connection.rollback();
            } finally {
                moveStatement.close();
                updateStatement.close();
                deleteStatement.close();
                connection.close();
            }

        } catch (SQLException ignored) {
        }
    }
}
