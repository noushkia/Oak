package com.oak.application.servlets;

import com.oak.application.Server;
import com.oak.application.service.ServiceLayer;
import com.oak.exception.User.NoUserLoggedIn;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "CommoditiesServlet", urlPatterns = { "/commodities" })
public class CommoditiesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (Server.getInstance().getServiceLayer().getCurrentUser() == null) {
            response.sendRedirect("/login");
        } else {
            request.getRequestDispatcher("/jsps/commodities.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServiceLayer serviceLayer = Server.getInstance().getServiceLayer();
        if (serviceLayer.getCurrentUser() == null) {
            response.sendRedirect("/login");
        }
        String action = request.getParameter("action");

        if (action.contains("search")) {
            serviceLayer.getCommodityService().setQuery(action, request.getParameter("search"));
        } else if (action.contains("sort")) {
            serviceLayer.getCommodityService().setComparator(action);
        } else if (action.equals("clear")) {
            serviceLayer.getCommodityService().reset();
        }

        response.sendRedirect("/commodities");
    }
}
