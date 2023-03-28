package com.oak.application.service;

import com.oak.domain.Comment;
import com.oak.domain.Commodity;
import com.oak.domain.User;
import com.oak.exception.Comment.CommentNotFound;
import com.oak.exception.User.UserNotFound;
import com.oak.data.Database;
import com.oak.exception.Commodity.CommodityNotFound;

public class CommentService extends Service{

    public CommentService(Database db) {
        super(db);
    }

    public void addComment(Comment comment) throws CommodityNotFound {
        Commodity commodity = db.fetchCommodity(comment.getCommodityId());
        db.addComment(comment);
        commodity.addComment(comment);
    }

    public void voteComment(String username, Integer commentId, Integer vote) throws UserNotFound, CommentNotFound {
        User user = db.fetchUser(username);
        Comment comment = db.fetchComment(commentId);
        comment.addUserVote(user.getUsername(), vote);
    }
}
