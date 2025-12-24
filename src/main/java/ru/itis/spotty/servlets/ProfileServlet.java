package ru.itis.spotty.servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.spotty.models.Place;
import ru.itis.spotty.models.Post;
import ru.itis.spotty.models.User;
import ru.itis.spotty.services.*;
import ru.itis.spotty.utils.CookieUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebServlet("/profile/*")
public class ProfileServlet extends HttpServlet {
    private SecurityService securityService;
    private FollowingService followingService;
    private PostService postService;
    private PlaceService placeService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.securityService = (SecurityService) config.getServletContext().getAttribute("securityService");
        this.followingService = (FollowingService) config.getServletContext().getAttribute("followingService");
        this.postService = (PostService) config.getServletContext().getAttribute("postService");
        this.placeService = (PlaceService) config.getServletContext().getAttribute("placeService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sessionId = CookieUtils.extractSessionId(request);
        User currentUser = null;
        if (sessionId != null) {
            try {
                currentUser = securityService.getUserBySessionId(UUID.fromString(sessionId));
            } catch (IllegalArgumentException ignored) {}
        }
        request.setAttribute("principal", currentUser);

        String pathInfo = request.getPathInfo();
        UUID targetUserId;
        User targetUser;

        if (pathInfo == null || "/".equals(pathInfo)) {
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            targetUserId = currentUser.getUserId();
            targetUser = currentUser;
            request.setAttribute("isOwnProfile", true);
        } else {
            String userIdStr = pathInfo.substring(1);
            try {
                targetUserId = UUID.fromString(userIdStr);
            } catch (IllegalArgumentException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный идентификатор пользователя");
                return;
            }
            targetUser = securityService.getUserById(targetUserId);
            if (targetUser == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Пользователь не найден");
                return;
            }
            boolean isOwnProfile = currentUser != null && currentUser.getUserId().equals(targetUserId);
            request.setAttribute("isOwnProfile", isOwnProfile);
        }

        int postsCount = postService.getPostsCount(targetUserId);
        int followersCount = followingService.countFollowers(targetUserId);
        int followingCount = followingService.countFollowedToId(targetUserId);

        request.setAttribute("user", targetUser);
        request.setAttribute("postsCount", postsCount);
        request.setAttribute("followersCount", followersCount);
        request.setAttribute("followingCount", followingCount);

        Boolean isOwnProfile = (Boolean) request.getAttribute("isOwnProfile");
        boolean isFollowing = false;
        if (Boolean.FALSE.equals(isOwnProfile) && currentUser != null) {
            isFollowing = followingService.isFollowing(currentUser.getUserId(), targetUserId);
        }
        request.setAttribute("isFollowing", isFollowing);

        List<Post> userPosts = new ArrayList<>();

        try {
            userPosts = postService.getPostsByUserId(targetUserId);

            for (Post post : userPosts) {
                UUID placeId = post.getPlaceId();
                Place place = placeService.getPlaceById(placeId);
                post.setPlaceName(place.getPlaceName() + ", " + place.getFullAddress());

                UUID authorId = post.getAuthorId();
                User author = securityService.getUserById(authorId);
                post.setAuthorUsername(author.getUsername());

                LocalDateTime createdAt = post.getCreatedAt();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                String formattedDate = createdAt.format(formatter);
                post.setFormattedCreatedAt(formattedDate);

                int countLikes = postService.countPostLikes(post.getPostId());
                post.setLikes(countLikes);

                if (currentUser != null) {
                    post.setCurrentUserLiked(postService.isCurrentUserLiked(post.getPostId(), currentUser.getUserId()));
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        String tab = request.getParameter("tab");
        if (Boolean.TRUE.equals(isOwnProfile)) {
            if ("liked".equals(tab)) {
                List<Post> liked = postService.getUserLikedPosts(targetUserId);

                for (Post post : liked) {
                    UUID placeId = post.getPlaceId();
                    Place place = placeService.getPlaceById(placeId);
                    post.setPlaceName(place.getPlaceName() + ", " + place.getFullAddress());

                    UUID authorId = post.getAuthorId();
                    User author = securityService.getUserById(authorId);
                    post.setAuthorUsername(author.getUsername());

                    LocalDateTime createdAt = post.getCreatedAt();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                    String formattedDate = createdAt.format(formatter);
                    post.setFormattedCreatedAt(formattedDate);

                    int countLikes = postService.countPostLikes(post.getPostId());
                    post.setLikes(countLikes);

                    post.setCurrentUserLiked(postService.isCurrentUserLiked(post.getPostId(), currentUser.getUserId()));
                }

                request.setAttribute("liked", liked);
            } else {
                request.setAttribute("myPosts", userPosts);
            }
        } else {
            request.setAttribute("myPosts", userPosts);
        }

        request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
    }
}