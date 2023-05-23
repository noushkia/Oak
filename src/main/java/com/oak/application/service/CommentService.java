package com.oak.application.service;

import com.oak.data.dao.CommentDAO;
import com.oak.data.dao.DAOLayer;
import com.oak.domain.Comment;
import com.oak.domain.Commodity;
import com.oak.domain.User;
import com.oak.exception.Comment.CommentNotFound;
import com.oak.exception.User.UserNotFound;
import com.oak.data.Database;
import com.oak.exception.Commodity.CommodityNotFound;

public class CommentService extends Service{

    public CommentService(Database db, DAOLayer daoLayer) {
        super(db, daoLayer);
    }

    public void addComment(Comment comment) throws CommodityNotFound {
        CommentDAO commentDAO = daoLayer.getCommentDAO();
        commentDAO.addComment(comment);
    }

    public void voteComment(String username, Integer commentId, Integer vote) throws UserNotFound, CommentNotFound {
        User user = db.fetchUser(username);
        Comment comment = db.fetchComment(commentId);
        comment.addUserVote(user.getUsername(), vote);
    }
}
