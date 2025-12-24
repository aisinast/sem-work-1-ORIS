package ru.itis.spotty.servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import ru.itis.spotty.exceptions.AccessDeniedException;
import ru.itis.spotty.models.Place;
import ru.itis.spotty.models.Post;
import ru.itis.spotty.models.Tag;
import ru.itis.spotty.utils.CookieUtils;
import ru.itis.spotty.utils.FileService;
import ru.itis.spotty.services.PlaceService;
import ru.itis.spotty.services.PostService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@WebServlet("/edit-post")
@MultipartConfig(
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 50
)
public class EditPostServlet extends HttpServlet {

    private PostService postService;
    private PlaceService placeService;
    private FileService fileService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.postService = (PostService) getServletContext().getAttribute("postService");
        this.placeService = (PlaceService) getServletContext().getAttribute("placeService");

        String uploadPath = config.getServletContext().getRealPath("/uploads");
        this.fileService = new FileService(uploadPath);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String postIdParam = request.getParameter("post_id");

        if (postIdParam == null || postIdParam.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Post ID is required");
            return;
        }

        Post post = postService.getPostById(UUID.fromString(postIdParam));
        if (post == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        UUID placeId = post.getPlaceId();
        Place place = placeService.getPlaceById(placeId);
        List<Tag> tags = postService.getAllTags();
        List<Tag> postTags = postService.getPostTags(post.getPostId());

        String selectedTagIds = postTags.stream()
                .map(tag -> tag.getTagId().toString())
                .reduce((id1, id2) -> id1 + "," + id2)
                .orElse("");

        request.setAttribute("post", post);
        request.setAttribute("place", place);
        request.setAttribute("tags", tags);
        request.setAttribute("postTags", postTags);
        request.setAttribute("selectedTagIds", selectedTagIds);
        request.getRequestDispatcher("/WEB-INF/views/edit-post.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String postIdParam = getPartValue(request, "post_id");
            String title = getPartValue(request, "title");
            String content = getPartValue(request, "content");
            String tagIdsParam = getPartValue(request, "tagIds");

            if (postIdParam == null || postIdParam.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Post ID is required");
                return;
            }

            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("Title is required");
            }
            if (content == null || content.trim().isEmpty()) {
                throw new IllegalArgumentException("Content is required");
            }

            UUID postId = UUID.fromString(postIdParam.trim());

            Post post = postService.getPostById(postId);
            if (post == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            post.setTitle(title.trim());
            post.setContent(content.trim());

            Part photoPart = request.getPart("photo");
            if (photoPart != null && photoPart.getSize() > 0) {
                String fileName = photoPart.getSubmittedFileName();
                if (fileName != null && !fileName.isEmpty()) {
                    String photoUrl = fileService.saveFile(photoPart.getInputStream(), fileName);
                    post.setImageUrl(photoUrl);
                }
            }

            try {
                UUID currentUserId = CookieUtils.extractUserIdFromSession(request);
                postService.updatePost(post, currentUserId);
            } catch (AccessDeniedException e) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
                return;
            }


            List<UUID> tagIds = parseTagIds(tagIdsParam);
            postService.updatePostTags(postId, tagIds);

            response.sendRedirect(request.getContextPath() + "/posts/" + postId);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Ошибка при обновлении поста: " + e.getMessage());

            String postIdParam = getPartValue(request, "post_id");
            if (postIdParam != null) {
                UUID postId = UUID.fromString(postIdParam.trim());
                Post post = postService.getPostById(postId);
                if (post != null) {
                    UUID placeId = post.getPlaceId();
                    Place place = placeService.getPlaceById(placeId);
                    List<Tag> tags = postService.getAllTags();
                    List<Tag> postTags = postService.getPostTags(postId);

                    String selectedTagIds = postTags.stream()
                            .map(tag -> tag.getTagId().toString())
                            .reduce((id1, id2) -> id1 + "," + id2)
                            .orElse("");

                    request.setAttribute("post", post);
                    request.setAttribute("place", place);
                    request.setAttribute("tags", tags);
                    request.setAttribute("selectedTagIds", selectedTagIds);
                }
            }

            request.getRequestDispatcher("/WEB-INF/views/edit-post.jsp").forward(request, response);
        }
    }

    private String getPartValue(HttpServletRequest request, String partName) throws IOException, ServletException {
        Part part = request.getPart(partName);
        if (part != null) {
            try (InputStream inputStream = part.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        }
        return null;
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