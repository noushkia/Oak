package com.oak.application.api;

import com.oak.application.service.AuthService;
import io.jsonwebtoken.Claims;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;

@Component
@Order(1)
@WebFilter(filterName = "LoggingFilter", urlPatterns = "/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        try {
            HashSet<String> unprotectedURLs = new HashSet<>();
            unprotectedURLs.add("http://127.0.0.1:3000/login");
            unprotectedURLs.add("http://127.0.0.1:3000/signup");
            unprotectedURLs.add("http://127.0.0.1:3000/callback");
            if (!unprotectedURLs.contains(httpServletRequest.getRequestURL().toString())) {
                String jwtString = httpServletRequest.getHeader("Authorization");
                Claims claims = AuthService.parseJWT(jwtString);
                httpServletRequest.setAttribute("username", claims.get("username").toString());
            }

            chain.doFilter(httpServletRequest, httpServletResponse);

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}