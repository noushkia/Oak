package com.oak.application.api;

import com.oak.application.service.AuthService;
import com.oak.exception.Auth.InvalidAuthorization;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.time.Instant;
import java.util.*;

@Component
@Order(2)
public class AuthFilter extends OncePerRequestFilter {

    private static final Set<String> UNPROTECTED_URLS = new HashSet<>(Arrays.asList(
            "http://localhost:3000/api/users/login",
            "http://localhost:3000/api/users/signUp",
            "http://localhost:3000/callback",
            "http://localhost:8080/api/users/login",
            "http://localhost:8080/api/users/signUp",
            "http://localhost:8080/callback"
    ));

    private Boolean validate(Claims claims) {
        return (Objects.equals(claims.getIssuer(), "Oak"))
                && (claims.getExpiration().after(Date.from(Instant.now())));
    }

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) {

        try {
            if (!UNPROTECTED_URLS.contains(request.getRequestURL().toString())) {
                String jwtString = request.getHeader("Authorization");
                Claims claims = AuthService.parseJWT(jwtString);
                if (!validate(claims)) {
                    throw new InvalidAuthorization();
                }
                request.setAttribute("username", claims.get("username").toString());
            }

            filterChain.doFilter(request, response);

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            response.setStatus(javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

}