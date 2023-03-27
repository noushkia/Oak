package com.oak.application.servlets;

import com.oak.application.Server;
import com.oak.application.service.ServiceLayer;
import com.oak.exception.Comment.CommentNotFound;
import com.oak.exception.Commodity.CommodityInBuyList;
import com.oak.exception.Commodity.CommodityNotFound;
import com.oak.exception.Commodity.CommodityOutOfStock;
import com.oak.exception.Commodity.InvalidRating;
import com.oak.exception.User.UserNotFound;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.StringTokenizer;

@WebServlet(name = "CommodityServlet", urlPatterns = {"/commodities/*"})
public class CommodityServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServiceLayer serviceLayer = Server.getInstance().getServiceLayer();

        if (serviceLayer.getCurrentUser() == null) {
            response.sendRedirect("/login");
        } else {
            StringTokenizer tokenizer = new StringTokenizer(request.getPathInfo(), "/");
            String commodityId = tokenizer.nextToken();
            try {
                serviceLayer.getCommodityService().getCommodityById(Integer.valueOf(commodityId));
            } catch (CommodityNotFound e) {
                throw new ServletException(e);
            }
            request.getRequestDispatcher("/jsps/commodity.jsp?commodityId=" + commodityId).forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServiceLayer serviceLayer = Server.getInstance().getServiceLayer();
        String username = serviceLayer.getCurrentUser().getUsername();

        StringTokenizer tokenizer = new StringTokenizer(request.getPathInfo(), "/");
        String action = tokenizer.nextToken();
        String commodityId = tokenizer.nextToken();

        switch (action) {
            case "rate" -> {
                try {
                    Integer score = Integer.valueOf(request.getParameter("score"));
                    serviceLayer.getCommodityService().getCommodityById(Integer.valueOf(commodityId))
                            .addUserRating(username, String.valueOf(score));
                } catch (InvalidRating | CommodityNotFound e) {
                    throw new ServletException(e);
                }
            }
            case "add" -> {
                try {
                    serviceLayer.getUserService().addToBuyList(username, Integer.valueOf(commodityId));
                    response.sendRedirect("/buyList");
                } catch (CommodityOutOfStock | UserNotFound | CommodityInBuyList | CommodityNotFound e) {
                    throw new ServletException(e);
                }
            }
            case "like" -> {
                try {
                    Integer commentId = Integer.valueOf(request.getParameter("comment_id"));
                    Integer vote = 1;
                    serviceLayer.getCommentService().voteComment(username, commentId, vote);
                } catch (UserNotFound | CommentNotFound e) {
                    throw new ServletException(e);
                }
            }
            case "dislike" -> {
                // TODO: 27.03.23 Duplication
                try {
                    Integer commentId = Integer.valueOf(request.getParameter("comment_id"));
                    Integer vote = -1;
                    serviceLayer.getCommentService().voteComment(username, commentId, vote);
                } catch (UserNotFound | CommentNotFound e) {
                    throw new ServletException(e);
                }
            }
            case "comment" -> {
                // TODO: 27.03.23 Implement Add Comment
            }
        }
        response.sendRedirect("/commodities/" + commodityId);
        // TODO: 27.03.23 Implement Recommended Commodities
    }
}
