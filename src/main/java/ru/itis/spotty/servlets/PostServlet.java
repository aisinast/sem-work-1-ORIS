package ru.itis.spotty.servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.spotty.models.Place;
import ru.itis.spotty.models.Post;
import ru.itis.spotty.models.Tag;
import ru.itis.spotty.models.User;
import ru.itis.spotty.services.PlaceService;
import ru.itis.spotty.services.PostService;
import ru.itis.spotty.services.SecurityService;
import ru.itis.spotty.utils.CookieUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@WebServlet("/posts/*")
public class PostServlet extends HttpServlet {

    private PostService postService;
    private PlaceService placeService;
    private SecurityService securityService;

    @Override
    public void init(ServletConfig config) {
        this.postService = (PostService) config.getServletContext().getAttribute("postService");
        this.placeService = (PlaceService) config.getServletContext().getAttribute("placeService");
        this.securityService = (SecurityService) config.getServletContext().getAttribute("securityService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || "/".equals(pathInfo)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        UUID postId;
        try {
            postId = UUID.fromString(pathInfo.substring(1));
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Post ID");
            return;
        }

        Post post = postService.getPostById(postId);
        if (post == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        UUID currentUserId = null;
        User currentUser = null;
        if (CookieUtils.extractSessionId(request) != null) {
            currentUserId = CookieUtils.extractUserIdFromSession(request);
            currentUser = securityService.getUserById(currentUserId);
        }

        Place place = placeService.getPlaceById(post.getPlaceId());

        post.setPlaceName(place.getPlaceName() + ", " + place.getFullAddress());

        UUID authorId = post.getAuthorId();
        User author = securityService.getUserById(authorId);
        post.setAuthorUsername(author.getUsername());

        LocalDateTime createdAt = post.getCreatedAt();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = createdAt.format(formatter);
        post.setFormattedCreatedAt(formattedDate);

        if (currentUser != null) {
            post.setCurrentUserLiked(postService.isCurrentUserLiked(post.getPostId(), currentUser.getUserId()));
        }

        int countLikes = postService.countPostLikes(postId);
        post.setLikes(countLikes);

        List<Tag> tags = postService.getPostTags(postId);

        request.setAttribute("post", post);
        request.setAttribute("currentUserId", currentUserId);
        request.setAttribute("tags", tags);
        request.getRequestDispatcher("/WEB-INF/views/post.jsp").forward(request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || "/".equals(pathInfo)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        UUID postId;
        try {
            postId = UUID.fromString(pathInfo.substring(1));
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Post ID");
            return;
        }

        UUID currentUserId = CookieUtils.extractUserIdFromSession(request);
        if (currentUserId == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Post post = postService.getPostById(postId);
        if (post == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (!post.getAuthorId().equals(currentUserId)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            postService.deletePost(postId);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to delete post");
        }
    }
}
