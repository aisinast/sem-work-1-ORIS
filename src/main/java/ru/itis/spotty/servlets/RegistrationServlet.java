package ru.itis.spotty.servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.spotty.exceptions.AuthenticationException;
import ru.itis.spotty.services.SecurityService;

import java.io.IOException;

@WebServlet("/registration")
public class RegistrationServlet extends HttpServlet {
    SecurityService securityService;

    @Override
    public void init(ServletConfig config) {
        this.securityService = (SecurityService) config.getServletContext().getAttribute("securityService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean hasSession = false;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("sessionId")) {
                    hasSession = true;
                    break;
                }
            }
        }

        if (hasSession) {
            response.sendRedirect(request.getContextPath() + "/profile");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/registration.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username").trim();
        String email = request.getParameter("email").trim();
        String password = request.getParameter("password").trim();
        String confirmPassword = request.getParameter("confirm_password").trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            request.setAttribute("error", "Пожалуйста, заполните все поля");
            request.getRequestDispatcher("/WEB-INF/views/registration.jsp").forward(request, response);
            return;
        }

        String sessionId;
        try {
            sessionId = String.valueOf(securityService.register(username, email, password, confirmPassword));
        } catch (AuthenticationException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("username", username);
            request.getRequestDispatcher("/WEB-INF/views/registration.jsp").forward(request, response);
            return;
        }
        Cookie cookie = new Cookie("sessionId", sessionId);
        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);
        response.sendRedirect(request.getContextPath() + "/profile");
    }
}
