package ru.itis.spotty.servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.spotty.models.Place;
import ru.itis.spotty.services.PlaceService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@WebServlet("/choose-place")
public class ChoosePlaceServlet extends HttpServlet {

    private PlaceService placeService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        this.placeService = (PlaceService) config.getServletContext().getAttribute("placeService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String currentPlaceId = request.getParameter("currentPlaceId");
        if (currentPlaceId != null) {
            try {
                Place currentPlace = placeService.getPlaceById(UUID.fromString(currentPlaceId));
                request.setAttribute("currentPlace", currentPlace);
            } catch (IllegalArgumentException _) {}
        }

        request.getRequestDispatcher("/WEB-INF/views/choose-place.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query");

        if (query == null || query.trim().isEmpty()) {
            request.getRequestDispatcher("/WEB-INF/views/choose-place.jsp").forward(request, response);
            return;
        }

        List<Place> places = placeService.findEligiblePlaces(query);
        request.setAttribute("places", places);

        request.getRequestDispatcher("/WEB-INF/views/choose-place.jsp").forward(request, response);
    }
}
