package ru.itis.spotty.servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import ru.itis.spotty.models.Place;
import ru.itis.spotty.models.Post;
import ru.itis.spotty.models.User;
import ru.itis.spotty.services.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

@WebServlet("/places/*")
public class PlacePageServlet extends HttpServlet {
    private PlaceService placeService;
    private PostService postService;
    private SecurityService securityService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.placeService = (PlaceServiceImpl) config.getServletContext().getAttribute("placeService");
        this.postService = (PostServiceImpl) config.getServletContext().getAttribute("postService");
        this.securityService = (SecurityServiceImpl) config.getServletContext().getAttribute("securityService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || "/".equals(pathInfo)) {
            response.sendRedirect(request.getContextPath() + "/main-page");
            return;
        }

        String idString = pathInfo.substring(1);
        if (idString.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/main-page");
            return;
        }

        UUID placeId = UUID.fromString(idString);

        Place place = placeService.getPlaceById(placeId);

        List<Post> posts = new ArrayList<>();

        try {
            posts = postService.getPostsByPlaceId(placeId);

            for (Post post : posts) {
                post.setPlaceName(place.getPlaceName() + ", " + place.getFullAddress());

                UUID authorId = post.getAuthorId();
                User author = securityService.getUserById(authorId);
                post.setAuthorUsername(author.getUsername());

                LocalDateTime createdAt = post.getCreatedAt();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                String formattedDate = createdAt.format(formatter);
                post.setFormattedCreatedAt(formattedDate);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        String address = place.getFullAddress();

        Properties properties = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("/application.properties");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String apikey = properties.getProperty("geocoder.api.key");
        String url = "https://geocode-maps.yandex.ru/1.x/?apikey=" + apikey +
                "&geocode=" + URLEncoder.encode(address, "UTF-8") + "&format=json";
        StringBuilder sb = new StringBuilder();
        URL apiUrl = new URL(url);
        BufferedReader in = new BufferedReader(new InputStreamReader(apiUrl.openStream()));
        String line;
        while ((line = in.readLine()) != null) sb.append(line);
        in.close();

        JSONObject json = new JSONObject(sb.toString());
        JSONArray features = json.getJSONObject("response")
                .getJSONObject("GeoObjectCollection")
                .getJSONArray("featureMember");

        if (features.length() > 0) {
            JSONObject geo = features.getJSONObject(0).getJSONObject("GeoObject");
            String pos = geo.getJSONObject("Point").getString("pos");
            String[] coords = pos.split(" ");
            String longitude = coords[0];
            String latitude = coords[1];
            place.setLatitude(Double.parseDouble(latitude));
            place.setLongitude(Double.parseDouble(longitude));
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }

        request.setAttribute("posts", posts);
        request.setAttribute("place", place);
        request.getRequestDispatcher("/WEB-INF/views/place-page.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
