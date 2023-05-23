package com.oak.data.dao;

import com.oak.data.ConnectionPool;
import com.oak.domain.Provider;
import com.oak.domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class ProviderDAO {
    public ProviderDAO() throws SQLException {
        Connection con = ConnectionPool.getConnection();
        Statement createTableStatement = con.createStatement();
        con.setAutoCommit(false);
        createTableStatement.addBatch(
                "CREATE TABLE IF NOT EXISTS Provider(id INT, name VARCHAR(50), " +
                        "registryDate DATETIME, image VARCHAR(255), " +
                        "PRIMARY KEY(id));"
        );
        createTableStatement.executeBatch();
        con.commit();
        createTableStatement.close();
        con.close();
    }

    private void fillProviderStatement(PreparedStatement userStatement, Provider provider) throws SQLException {
        userStatement.setInt(1, provider.getId());
        userStatement.setString(2, provider.getName());
        userStatement.setDate(3, new java.sql.Date(provider.getRegistryDate().getTime()));
        userStatement.setString(4, provider.getImage());
    }

    public void addProvider(Provider provider) {
        try {
            Connection con = ConnectionPool.getConnection();
            con.setAutoCommit(false);
            PreparedStatement providerStatement = con.prepareStatement(
                    "INSERT INTO Provider(id, name, registryDate, image) "
                            + " VALUES(?,?,?,?)"
                            + "ON DUPLICATE KEY UPDATE "
                            + "name = VALUES(name), "
                            + "registryDate = VALUES(registryDate), "
                            + "image = VALUES(image);"
            );
            fillProviderStatement(providerStatement, provider);
            try {
                providerStatement.execute();
                con.commit();
            } catch (Exception e) {
                con.rollback();
            } finally {
                providerStatement.close();
                con.close();
            }
        } catch (SQLException ignored) {}
}
