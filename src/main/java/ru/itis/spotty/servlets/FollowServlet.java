package ru.itis.spotty.servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.spotty.models.User;
import ru.itis.spotty.services.FollowingService;
import ru.itis.spotty.services.SecurityService;
import ru.itis.spotty.utils.CookieUtils;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/profile/follow")
public class FollowServlet extends HttpServlet {
    private FollowingService followingService;
    private SecurityService securityService;

    @Override
    public void init(ServletConfig config) {
        followingService = (FollowingService) config.getServletContext().getAttribute("followingService");
        securityService = (SecurityService) config.getServletContext().getAttribute("securityService");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String sessionId = CookieUtils.extractSessionId(request);

        if (sessionId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User currentUser;
        try {
            currentUser = securityService.getUserBySessionId(UUID.fromString(sessionId));
        } catch (IllegalArgumentException e) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String targetUserIdStr = request.getParameter("userId");
        if (targetUserIdStr == null || targetUserIdStr.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Требуется айди пользователя");
            return;
        }

        UUID targetUserId;
        try {
            targetUserId = UUID.fromString(targetUserIdStr);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный айди пользователя");
            return;
        }

        if (currentUser.getUserId().equals(targetUserId)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Вы не можете подписаться на себя или отписаться от себя");
            return;
        }

        try {
            boolean isFollowing = followingService.isFollowing(currentUser.getUserId(), targetUserId);

            if (isFollowing) {
                followingService.unfollow(currentUser.getUserId(), targetUserId);
            } else {
                followingService.follow(currentUser.getUserId(), targetUserId);
            }
            response.sendRedirect(request.getContextPath() + "/profile/" + targetUserId);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
        }
    }
}