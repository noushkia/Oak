package com.oak.application.servlets;

import com.oak.application.Server;
import com.oak.application.service.ServiceLayer;
import com.oak.exception.User.NegativeCredit;
import com.oak.exception.User.NoUserLoggedIn;
import com.oak.exception.User.UserNotFound;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "CreditServlet", urlPatterns = { "/credit" })
public class CreditServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (Server.getInstance().getServiceLayer().getCurrentUser() == null) {
            response.sendRedirect("/login");
        } else {
            request.getRequestDispatcher("/jsps/credit.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServiceLayer serviceLayer = Server.getInstance().getServiceLayer();
        if (serviceLayer.getCurrentUser() == null) {
            response.sendRedirect("/login");
        }
        try {
            Integer credit = Integer.parseInt(request.getParameter("credit"));
            String username = serviceLayer.getCurrentUser().getUsername();
            serviceLayer.getUserService().addCredit(username, credit);
        } catch (UserNotFound | NegativeCredit e) {
            throw new ServletException(e);
        }
        response.sendRedirect("/");
    }
}
