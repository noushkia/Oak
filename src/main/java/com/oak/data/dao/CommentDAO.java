package com.oak.data.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oak.data.ConnectionPool;
import com.oak.domain.Comment;
import com.oak.exception.Comment.CommentNotFound;
import com.oak.exception.Commodity.CommodityNotFound;
import com.oak.exception.User.UserNotFound;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
                            + "VALUES(?,?,?,?,?) "
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

    private Comment createComment(ResultSet result) throws SQLException, JsonProcessingException {
        int id = result.getInt("id");
        String userEmail = result.getString("userEmail");
        int commodityId = result.getInt("commodityId");
        String text = result.getString("text");
        Date date = result.getDate("date");

        Comment comment = new Comment(userEmail, commodityId, text, date);
        comment.setId(id);
        return comment;
    }

    public Integer fetchNewestId() {
        int newestCommentId = -1;
        try {
            Connection con = ConnectionPool.getConnection();
            con.setAutoCommit(false);
            PreparedStatement getCommentsStatement = con.prepareStatement(
                    "SELECT MAX(id) FROM Comment;"
            );
            try {
                ResultSet result = getCommentsStatement.executeQuery();
                if (result.next()) {
                    newestCommentId = result.getInt("MAX(id)");
                }
            } catch (SQLException e) {
                con.rollback();
            } finally {
                getCommentsStatement.close();
                con.close();
            }
        } catch (SQLException ignored) {
        }
        return newestCommentId;
    }

    public List<Comment> fetchComments(Integer commodityId) {
        ArrayList<Comment> comments = new ArrayList<>();
        try {
            Connection con = ConnectionPool.getConnection();
            con.setAutoCommit(false);
            PreparedStatement getCommentsStatement = con.prepareStatement(
                    "SELECT * FROM Comment "
                            + "WHERE commodityId = ?;"
            );
            getCommentsStatement.setInt(1, commodityId);
            try {
                ResultSet result = getCommentsStatement.executeQuery();
                while (result.next()) {
                    comments.add(createComment(result));
                }
            } catch (SQLException | JsonProcessingException e) {
                con.rollback();
            } finally {
                getCommentsStatement.close();
                con.close();
            }
        } catch (SQLException ignored) {
        }
        return comments;
    }

    public HashMap<String, Integer> fetchVotes(Integer commentId) {
        HashMap<String, Integer> votes = new HashMap<>();
        try {
            Connection con = ConnectionPool.getConnection();
            con.setAutoCommit(false);
            PreparedStatement getVotesStatement = con.prepareStatement(
                    "SELECT * FROM Vote "
                            + "WHERE commentId = ?;"
            );
            getVotesStatement.setInt(1, commentId);
            try {
                ResultSet result = getVotesStatement.executeQuery();
                while (result.next()) {
                    String username = result.getString("username");
                    Integer vote = result.getInt("vote");
                    votes.put(username, vote);
                }
            } catch (SQLException e) {
                con.rollback();
            } finally {
                getVotesStatement.close();
                con.close();
            }
        } catch (SQLException ignored) {
        }
        return votes;
    }

    public void addUserVote(String username, Integer commentId, Integer vote) throws UserNotFound, CommentNotFound {
        try {
            Connection connection = ConnectionPool.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement addUserVoteStatement = connection.prepareStatement(
                    "INSERT INTO Vote(username, commentId, vote) " +
                            "VALUES (?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE " +
                            "vote = VALUES(vote);"
            );
            addUserVoteStatement.setString(1, username);
            addUserVoteStatement.setInt(2, commentId);
            addUserVoteStatement.setInt(3, vote);

            try {
                addUserVoteStatement.executeUpdate();
                connection.commit();
                addUserVoteStatement.close();
                connection.close();
            } catch (SQLException sqlException) {
                connection.rollback();
                addUserVoteStatement.close();
                connection.close();
                if (sqlException.getSQLState().equals("23000")) {
                    String errorMessage = sqlException.getMessage();
                    if (errorMessage.contains("User")) {
                        throw new UserNotFound(username);
                    } else if (errorMessage.contains("Comment")) {
                        throw new CommentNotFound(commentId);
                    }
                }
            }
        } catch (SQLException ignored) {
        }
    }
}
