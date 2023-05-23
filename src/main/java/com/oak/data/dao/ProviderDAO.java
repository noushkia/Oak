package com.oak.data.dao;

import com.oak.data.ConnectionPool;

import java.sql.Connection;
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
}
