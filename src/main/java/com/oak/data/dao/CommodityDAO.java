package com.oak.data.dao;

import com.oak.data.ConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CommodityDAO {
    public CommodityDAO() throws SQLException {
        Connection con = ConnectionPool.getConnection();
        Statement createTableStatement = con.createStatement();
        con.setAutoCommit(false);
        createTableStatement.addBatch(
                "CREATE TABLE IF NOT EXISTS Commodity(id INT, name VARCHAR(50), " +
                        "providerId INT, price INT, categories TEXT, " +
                        "rating FLOAT, inStock INT, image VARCHAR(255)," +
                        "PRIMARY KEY(id)," +
                        "FOREIGN KEY (providerId) REFERENCES Provider(id));"
        );
        createTableStatement.executeBatch();
        con.commit();
        createTableStatement.close();
        con.close();
    }
}
