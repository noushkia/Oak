package com.oak.application.servlets;

import com.oak.application.Server;
import com.oak.application.service.ServiceLayer;
import com.oak.domain.User;
import com.oak.exception.User.InvalidCredentials;
import com.oak.exception.User.UserNotFound;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = { "/login" })
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/jsps/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        ServiceLayer serviceLayer = Server.getInstance().getServiceLayer();
        try {
            User user = serviceLayer.getUserService().login(username, password);
            serviceLayer.setUser(user);
            response.sendRedirect("/");
        } catch (UserNotFound | InvalidCredentials ignored) {
        }
    }
}
