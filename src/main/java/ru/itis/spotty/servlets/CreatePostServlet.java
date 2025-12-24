package ru.itis.spotty.servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import ru.itis.spotty.models.Place;
import ru.itis.spotty.models.Post;
import ru.itis.spotty.models.User;
import ru.itis.spotty.utils.FileService;
import ru.itis.spotty.services.PlaceService;
import ru.itis.spotty.services.PostService;
import ru.itis.spotty.services.SecurityService;
import ru.itis.spotty.utils.CookieUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@WebServlet("/posts/create")
@MultipartConfig(
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 15,
        fileSizeThreshold = 1024 * 1024
)
public class CreatePostServlet extends HttpServlet {
    private PostService postService;
    private SecurityService securityService;
    private FileService fileService;
    private PlaceService placeService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.postService = (PostService) config.getServletContext().getAttribute("postService");
        this.securityService = (SecurityService) config.getServletContext().getAttribute("securityService");
        this.placeService = (PlaceService) config.getServletContext().getAttribute("placeService");

        String uploadPath = config.getServletContext().getRealPath("/uploads");
        this.fileService = new FileService(uploadPath);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

        try {
            request.setAttribute("tags", postService.getAllTags());
        } catch (Exception e) {
            request.setAttribute("tags", List.of());
        }

        String placeIdStr = request.getParameter("placeId");
        Place place = null;

        if (placeIdStr != null && !placeIdStr.trim().isEmpty()) {
            try {
                UUID placeId = UUID.fromString(placeIdStr);
                place = placeService.getPlaceById(placeId);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        request.setAttribute("place", place);
        request.getRequestDispatcher("/WEB-INF/views/create-post.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

        try {
            String title = request.getParameter("title");
            String content = request.getParameter("content");
            String placeIdStr = request.getParameter("placeId");
            String tagIdsParam = request.getParameter("tagIds");

            Part photoPart = request.getPart("photo");
            String photoUrl = null;

            if (photoPart != null && photoPart.getSize() > 0) {
                String fileName = photoPart.getSubmittedFileName();
                if (fileName != null && !fileName.isEmpty()) {
                    photoUrl = fileService.saveFile(photoPart.getInputStream(), fileName);
                }
            }

            if (title == null || title.trim().isEmpty()) {
                request.setAttribute("error", "Заголовок обязателен для заполнения");
                request.setAttribute("tags", postService.getAllTags());
                request.setAttribute("title", title);
                request.setAttribute("content", content);
                request.getRequestDispatcher("/WEB-INF/views/create-post.jsp").forward(request, response);
                return;
            }

            if (content == null || content.trim().isEmpty()) {
                request.setAttribute("error", "Текст поста обязателен для заполнения");
                request.setAttribute("tags", postService.getAllTags());
                request.setAttribute("title", title);
                request.setAttribute("content", content);
                request.getRequestDispatcher("/WEB-INF/views/create-post.jsp").forward(request, response);
                return;
            }

            if (photoUrl == null) {
                request.setAttribute("error", "Фотография обязательна для поста");
                request.setAttribute("tags", postService.getAllTags());
                request.setAttribute("title", title);
                request.setAttribute("content", content);
                request.getRequestDispatcher("/WEB-INF/views/create-post.jsp").forward(request, response);
                return;
            }

            UUID placeId = null;
            if (placeIdStr != null && !placeIdStr.trim().isEmpty()) {
                try {
                    placeId = UUID.fromString(placeIdStr);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    request.setAttribute("error", "Ошибка при создании поста: " + e.getMessage());
                    request.setAttribute("tags", postService.getAllTags());
                    request.setAttribute("title", request.getParameter("title"));
                    request.setAttribute("content", request.getParameter("content"));
                    request.getRequestDispatcher("/WEB-INF/views/create-post.jsp").forward(request, response);
                    return;
                }
            }

            List<UUID> tagIds = parseTagIds(tagIdsParam);

            Post post = new Post(
                    title.trim(),
                    content.trim(),
                    currentUser.getUserId(),
                    placeId,
                    photoUrl
            );

            UUID postId = postService.addPost(post);

            for (UUID tagId : tagIds) {
                postService.addPostTag(postId, tagId);
            }

            response.sendRedirect(request.getContextPath() + "/main-page");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Ошибка при создании поста: " + e.getMessage());
            request.setAttribute("tags", postService.getAllTags());
            request.setAttribute("title", request.getParameter("title"));
            request.setAttribute("content", request.getParameter("content"));
            request.getRequestDispatcher("/WEB-INF/views/create-post.jsp").forward(request, response);
        }
    }

    private List<UUID> parseTagIds(String tagIdsParam) {
        if (tagIdsParam == null || tagIdsParam.trim().isEmpty()) {
            return List.of();
        }

        return Arrays.stream(tagIdsParam.split(","))
                .filter(id -> !id.trim().isEmpty())
                .map(UUID::fromString)
                .collect(Collectors.toList());
    }
}