package com.oak.data.dao;

import com.oak.data.ConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CommentDAO {
    public CommentDAO() throws SQLException {
        Connection con = ConnectionPool.getConnection();
        Statement createTableStatement = con.createStatement();
        con.setAutoCommit(false);
        createTableStatement.addBatch(
                "CREATE TABLE IF NOT EXISTS Comment(id INT, userEmail VARCHAR(50), " +
                        "commodityId INT, text VARCHAR(255), date DATETIME," +
                        "PRIMARY KEY(id)," +
                        "FOREIGN KEY (commodityId) REFERENCES Commodity(id));"
        );
        createTableStatement.executeBatch();
        con.commit();
        createTableStatement.close();
        con.close();
    }
}
