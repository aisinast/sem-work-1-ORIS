package ru.itis.spotty.servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import ru.itis.spotty.models.User;
import ru.itis.spotty.utils.FileService;
import ru.itis.spotty.services.SecurityService;
import ru.itis.spotty.services.UserService;
import ru.itis.spotty.utils.CookieUtils;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/profile/edit")
@MultipartConfig(
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 10
)
public class EditProfileServlet extends HttpServlet {
    private SecurityService securityService;
    private UserService userService;
    private FileService fileService;

    @Override
    public void init(ServletConfig config) {
        this.securityService = (SecurityService) config.getServletContext().getAttribute("securityService");
        this.userService = (UserService) config.getServletContext().getAttribute("userService");

        String uploadPath = config.getServletContext().getRealPath("/uploads");
        this.fileService = new FileService(uploadPath);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            UUID currUserId = CookieUtils.extractUserIdFromSession(request);

            if (currUserId == null) {
                response.sendRedirect("/login");
                return;
            }

            User user = securityService.getUserById(currUserId);

            if (user == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Пользователь не найден");
                return;
            }

            request.setAttribute("user", user);
            request.getRequestDispatcher("/WEB-INF/views/edit-profile.jsp").forward(request, response);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка сервера");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            // тк юзер берется из сессии, можно отредактировать только принадлежащий себе профиль
            UUID currUserId = CookieUtils.extractUserIdFromSession(request);
            if (currUserId == null) {
                response.sendRedirect("/login");
                return;
            }

            String username = request.getParameter("username");
            String email = request.getParameter("email");
            String bio = request.getParameter("bio");
            String avatarUrl = null;

            Part avatarPart = request.getPart("avatar");
            if (avatarPart != null && avatarPart.getSize() > 0) {
                String fileName = avatarPart.getSubmittedFileName();
                if (fileName != null && !fileName.isEmpty()) {
                    avatarUrl = fileService.saveFile(avatarPart.getInputStream(), fileName);
                }
            }

            userService.editProfile(currUserId, username, email, bio, avatarUrl);

            response.sendRedirect(request.getContextPath() + "/profile");
        } catch (RuntimeException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/edit-profile.jsp").forward(request, response);
        }
    }
}
