package ru.itis.spotty.servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.spotty.models.Place;
import ru.itis.spotty.services.PlaceService;
import ru.itis.spotty.services.PlaceServiceImpl;
import java.io.IOException;
import java.util.UUID;

@WebServlet("/create-place")
public class CreatePlaceServlet extends HttpServlet {

    private PlaceService placeService;

    @Override
    public void init(ServletConfig config) {
        placeService = (PlaceServiceImpl) config.getServletContext().getAttribute("placeService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/create-place.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String placeName = request.getParameter("place_name").trim();
        String country = request.getParameter("country").trim();
        String city = request.getParameter("city").trim();
        String street = request.getParameter("street").trim();
        String houseNumber = request.getParameter("house_number").trim();

        if (placeName.isEmpty() || country.isEmpty() || city.isEmpty() || street.isEmpty() || houseNumber.isEmpty()) {
            request.setAttribute("error", "Пожалуйста, заполните все поля");
            request.setAttribute("place_name", placeName);
            request.setAttribute("country", country);
            request.setAttribute("city", city);
            request.setAttribute("street", street);
            request.setAttribute("house_number", houseNumber);
            request.getRequestDispatcher("/WEB-INF/views/create-place.jsp").forward(request, response);
            return;
        }

        String fullAddress;
        if ("-".equals(houseNumber)) {
            fullAddress = String.format("%s, %s, %s", country, city, street);
        } else {
            fullAddress = String.format("%s, %s, %s, %s", country, city, street, houseNumber);
        }

        Place place = new Place(placeName, fullAddress);
        UUID placeId = placeService.addPlace(place);

        if (placeId != null) {
            response.sendRedirect(request.getContextPath() + "/posts/create?placeId=" + placeId);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/posts/create");
    }
}
