package ru.itis.spotty.servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.spotty.models.Place;
import ru.itis.spotty.models.Post;
import ru.itis.spotty.models.PostPage;
import ru.itis.spotty.models.User;
import ru.itis.spotty.services.PlaceService;
import ru.itis.spotty.services.PostService;
import ru.itis.spotty.services.SecurityService;
import ru.itis.spotty.utils.CookieUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@WebServlet("/main-page")
public class MainPageServlet extends HttpServlet {
    private SecurityService securityService;
    private PostService postService;
    private PlaceService placeService;

    @Override
    public void init(ServletConfig config) {
        this.securityService = (SecurityService) config.getServletContext().getAttribute("securityService");
        this.postService = (PostService) config.getServletContext().getAttribute("postService");
        this.placeService = (PlaceService) config.getServletContext().getAttribute("placeService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int page;
        int pageSize;

        try {
            page = Integer.parseInt(request.getParameter("page"));
        } catch (NumberFormatException e) {
            page = 1;
        }

        try {
            pageSize = Integer.parseInt(request.getParameter("pageSize"));
        } catch (NumberFormatException e) {
            pageSize = 6;
        }

        UUID currentUserId = CookieUtils.extractUserIdFromSession(request);

        PostPage postPage = postService.getPostPage(page, pageSize);

        for (Post post : postPage.getPosts()) {
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

            if (currentUserId != null) {
                post.setCurrentUserLiked(postService.isCurrentUserLiked(post.getPostId(), currentUserId));
            }
        }

        request.setAttribute("posts", postPage.getPosts());
        request.setAttribute("postPage", postPage);

        request.getRequestDispatcher("/WEB-INF/views/main-page.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {

    }
}
