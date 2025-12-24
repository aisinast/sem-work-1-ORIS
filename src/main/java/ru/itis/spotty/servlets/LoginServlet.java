package ru.itis.spotty.servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.spotty.services.SecurityService;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private SecurityService securityService;

    @Override
    public void init(ServletConfig config) {
        this.securityService = (SecurityService) config.getServletContext().getAttribute("securityService");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username").trim();
        String password = request.getParameter("password").trim();

        if (username.isEmpty() || password.isEmpty()) {
            request.setAttribute("error", "Пожалуйста, введите логин и пароль");
            request.getRequestDispatcher("WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        String sessionId;

        try {
            sessionId = String.valueOf(securityService.login(username, password));
        } catch (Exception e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("username", username);
            request.getRequestDispatcher("WEB-INF/views/login.jsp").forward(request, response);
            return;
        }

        Cookie cookie = new Cookie("sessionId", sessionId);
        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);
        response.sendRedirect(request.getContextPath() + "/profile");
    }
}
