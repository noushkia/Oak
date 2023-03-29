package com.oak.application.servlets;

import com.oak.application.Server;
import com.oak.application.service.ServiceLayer;
import com.oak.domain.Comment;
import com.oak.exception.Comment.CommentNotFound;
import com.oak.exception.Commodity.CommodityInBuyList;
import com.oak.exception.Commodity.CommodityNotFound;
import com.oak.exception.Commodity.CommodityOutOfStock;
import com.oak.exception.Commodity.InvalidRating;
import com.oak.exception.Discount.DiscountNotFound;
import com.oak.exception.Discount.ExpiredDiscount;
import com.oak.exception.User.InsufficientCredit;
import com.oak.exception.User.UserNotFound;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.Date;
import java.util.StringTokenizer;

@WebServlet(name = "BuyListServlet", urlPatterns = { "/buyList" })
public class BuyListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (Server.getInstance().getServiceLayer().getCurrentUser() == null) {
            response.sendRedirect("/login");
        } else {
            request.getRequestDispatcher("/jsps/buyList.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServiceLayer serviceLayer = Server.getInstance().getServiceLayer();
        if (serviceLayer.getCurrentUser() == null) {
            response.sendRedirect("/login");
        }
        String username = serviceLayer.getCurrentUser().getUsername();

        String action = request.getParameter("action");

        switch (action) {
            case "pay" -> {
                try {
                    serviceLayer.getUserService().finalizeBuyList(username);
                } catch (UserNotFound | InsufficientCredit | CommodityOutOfStock e) {
                    throw new ServletException(e);
                }
            }
            case "remove" -> {
                try {
                    Integer commodityId = Integer.valueOf(request.getParameter("commodity_id"));
                    serviceLayer.getUserService().removeFromBuyList(username, commodityId);
                } catch (UserNotFound | CommodityNotFound e) {
                    throw new ServletException(e);
                }
            }
            case "discount" -> {
                try {
                    String discountCode = request.getParameter("code");
                    serviceLayer.getUserService().addDiscount(username, discountCode);
                } catch (UserNotFound | DiscountNotFound | ExpiredDiscount e) {
                    throw new ServletException(e);
                }
            }
        }
        response.sendRedirect("/buyList");
    }
}
