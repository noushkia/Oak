package com.oak.application.service;

import com.oak.data.dao.CommentDAO;
import com.oak.data.dao.DAOLayer;
import com.oak.domain.Comment;
import com.oak.exception.Comment.CommentNotFound;
import com.oak.exception.User.UserNotFound;
import com.oak.data.Database;
import com.oak.exception.Commodity.CommodityNotFound;

public class CommentService extends Service {

    public CommentService(Database db, DAOLayer daoLayer) {
        super(db, daoLayer);
    }

    public void addComment(Comment comment) throws CommodityNotFound {
        comment.setId();
        CommentDAO commentDAO = daoLayer.getCommentDAO();
        commentDAO.addComment(comment);
    }

    public void setNewCommentId() {
        CommentDAO commentDAO = daoLayer.getCommentDAO();
        Integer newCommentId = commentDAO.fetchNewestId();
        Comment.setNewCommentId(newCommentId+1);
    }

    public void voteComment(String username, Integer commentId, Integer vote) throws UserNotFound, CommentNotFound {
        daoLayer.getCommentDAO().addUserVote(username, commentId, vote);
    }
}
