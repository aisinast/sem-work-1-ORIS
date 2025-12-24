package ru.itis.spotty.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.spotty.exceptions.AuthenticationException;
import ru.itis.spotty.models.User;
import ru.itis.spotty.services.SecurityService;
import ru.itis.spotty.utils.CookieUtils;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@WebFilter("/*")
public class AuthFilter extends HttpFilter {
    private final List<String> protectedUrls;
    private SecurityService securityService;

    public AuthFilter() {
        this.protectedUrls = List.of("/profile");
    }

    @Override
    public void init(FilterConfig config) {
        this.securityService = (SecurityService) config.getServletContext().getAttribute("securityService");
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String sessionId = CookieUtils.extractSessionId(request);
        User user = null;

        if (sessionId != null) {
            try {
                user = securityService.getUserBySessionId(UUID.fromString(sessionId));
                request.setAttribute("user", user);
                request.setAttribute("principal", user);
            } catch (AuthenticationException ignored) {}

            if (protectedUrls.contains(request.getRequestURI()) && user == null) {
                response.sendRedirect("/login");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
