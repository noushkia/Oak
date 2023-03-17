package org.ie.tk.application.service;

import org.ie.tk.data.Database;
import org.ie.tk.domain.Comment;
import org.ie.tk.domain.Commodity;
import org.ie.tk.domain.User;
import org.ie.tk.exception.Comment.CommentNotFound;
import org.ie.tk.exception.Commodity.CommodityNotFound;
import org.ie.tk.exception.User.UserNotFound;

public class CommentService extends Service{

    public CommentService(Database db) {
        super(db);
    }

    public void addComment(Comment comment) throws CommodityNotFound {
        Commodity commodity = db.fetchCommodity(comment.getCommodityId());
        commodity.addComment(comment);
        db.addComment(comment);
    }

    public void voteComment(String username, Integer commentId, Integer vote) throws UserNotFound, CommentNotFound {
        User user = db.fetchUser(username);
        Comment comment = db.fetchComment(commentId);
        comment.addUserVote(user.getUsername(), vote);
    }
}
