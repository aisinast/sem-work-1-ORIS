package ru.itis.spotty.servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.spotty.services.SecurityService;
import ru.itis.spotty.utils.CookieUtils;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    SecurityService securityService;

    @Override
    public void init(ServletConfig config) {
        securityService = (SecurityService) config.getServletContext().getAttribute("securityService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String sessionId = CookieUtils.extractSessionId(request);

        if (sessionId != null) {
            securityService.logout(UUID.fromString(sessionId));

            Cookie sessionCookie = new Cookie("sessionId", "");
            sessionCookie.setMaxAge(0);
            response.addCookie(sessionCookie);
        }

        response.sendRedirect(request.getContextPath() + "/main-page.jsp");
    }
}
