package com.oak.application.servlets;

import com.oak.application.Server;
import com.oak.exception.User.NoUserLoggedIn;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "LogoutServlet", urlPatterns = { "/logout" })
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (Server.getInstance().getServiceLayer().getCurrentUser() == null) {
            throw new ServletException(new NoUserLoggedIn());
        } else {
            Server.getInstance().getServiceLayer().setUser(null);
            response.sendRedirect("/login");
        }
    }
}
