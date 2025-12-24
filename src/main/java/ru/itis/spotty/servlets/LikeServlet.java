package ru.itis.spotty.servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.spotty.services.PostService;
import ru.itis.spotty.utils.CookieUtils;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/like")
public class LikeServlet extends HttpServlet {
    private PostService postService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.postService = (PostService) config.getServletContext().getAttribute("postService");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String postIdParam = request.getParameter("postId");

        if (postIdParam == null || postIdParam.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"error\": \"Post ID is required\"}");
            return;
        }

        try {
            UUID postId = UUID.fromString(postIdParam);

            UUID currentUserId = CookieUtils.extractUserIdFromSession(request);

            if (currentUserId == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": false, \"error\": \"Not authorized\"}");
                return;
            }

            postService.addLike(postId, currentUserId);

            int likesCount = postService.countPostLikes(postId);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            String jsonResponse = String.format(
                    "{\"success\": true, \"likes\": %d, \"isLiked\": true}",
                    likesCount
            );
            response.getWriter().write(jsonResponse);

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"error\": \"Invalid post ID format\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"error\": \"Server error: " + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String postIdParam = request.getParameter("postId");

        if (postIdParam == null || postIdParam.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"error\": \"Post ID is required\"}");
            return;
        }

        try {
            UUID postId = UUID.fromString(postIdParam);

            UUID currentUserId = CookieUtils.extractUserIdFromSession(request);

            if (currentUserId == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": false, \"error\": \"Not authorized\"}");
                return;
            }

            postService.removeLike(postId, currentUserId);

            int likesCount = postService.countPostLikes(postId);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            String jsonResponse = String.format(
                    "{\"success\": true, \"likes\": %d, \"isLiked\": false}",
                    likesCount
            );
            response.getWriter().write(jsonResponse);

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"error\": \"Invalid post ID format\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"error\": \"Server error: " + e.getMessage() + "\"}");
        }
    }
}