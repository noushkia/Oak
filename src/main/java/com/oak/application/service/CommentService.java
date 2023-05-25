package com.oak.application.service;

import com.oak.data.dao.CommentDAO;
import com.oak.data.dao.CommodityDAO;
import com.oak.data.dao.DAOLayer;
import com.oak.data.dao.UserDAO;
import com.oak.domain.BuyList;
import com.oak.domain.Comment;
import com.oak.domain.CommodityList;
import com.oak.domain.User;
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

    public void voteComment(String username, Integer commentId, Integer vote) throws UserNotFound, CommentNotFound {
        UserDAO userDAO = daoLayer.getUserDAO();
        CommodityDAO commodityDAO = daoLayer.getCommodityDAO();
        CommentDAO commentDAO = daoLayer.getCommentDAO();

        User user = userDAO.fetchUser(username);
        BuyList buyList = (BuyList) userDAO.fetchUserList(username, "BuyList", commodityDAO);
        CommodityList purchasedList = userDAO.fetchUserList(username, "PurchasedList", commodityDAO);
        user.getBuylist().update(buyList);
        user.getPurchasedList().update(purchasedList);

        commentDAO.addUserVote(username, commentId, vote);
    }
}
