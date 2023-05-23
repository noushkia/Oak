package com.oak.data.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oak.data.ConnectionPool;
import com.oak.domain.Comment;
import com.oak.exception.Commodity.CommodityNotFound;

import java.sql.Connection;
import java.sql.PreparedStatement;
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

    private void fillCommentStatement(PreparedStatement commentStatement, Comment comment) throws SQLException, JsonProcessingException {
        commentStatement.setInt(1, comment.getId());
        commentStatement.setString(2, comment.getUserEmail());
        commentStatement.setInt(3, comment.getCommodityId());
        commentStatement.setString(4, comment.getText());
        commentStatement.setDate(5, new java.sql.Date(comment.getDate().getTime()));
    }

    public void addComment(Comment comment) throws CommodityNotFound {
        try {
            Connection con = ConnectionPool.getConnection();
            con.setAutoCommit(false);
            PreparedStatement commentStatement = con.prepareStatement(
                    "INSERT INTO Comment(id, userEmail, commodityId, text, date) "
                            + " VALUES(?,?,?,?,?)"
                            + "ON DUPLICATE KEY UPDATE "
                            + "userEmail = VALUES(userEmail), "
                            + "commodityId = VALUES(commodityId), "
                            + "text = VALUES(text), "
                            + "date = VALUES(date);"
            );
            fillCommentStatement(commentStatement, comment);
            try {
                commentStatement.execute();
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                if (e.getSQLState().equals("23503")) {
                    commentStatement.close();
                    con.close();
                    throw new CommodityNotFound(comment.getCommodityId());
                }
            } finally {
                commentStatement.close();
                con.close();
            }
        } catch (SQLException | JsonProcessingException ignored) {
        }
    }
}
