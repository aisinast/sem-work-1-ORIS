package ru.itis.spotty.servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.itis.spotty.models.Place;
import ru.itis.spotty.models.User;
import ru.itis.spotty.services.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/search")
public class SearchPageServlet extends HttpServlet {

    private PlaceService placeService;
    private UserService userService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.placeService = (PlaceServiceImpl) getServletContext().getAttribute("placeService");
        this.userService = (UserServiceImpl) getServletContext().getAttribute("userService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String query = request.getParameter("query");
        String tab = request.getParameter("tab");

        if (query == null || (query = query.trim()).isEmpty()) {
            request.setAttribute("error", "Запрос не может быть пустым");
            request.getRequestDispatcher("/WEB-INF/views/search-page.jsp").forward(request, response);
            return;
        }

        tab = (tab == null || tab.isBlank()) ? "accounts" : tab.trim();
        if ("accounts".equals(tab)) {
            List<User> eligibleUsers = userService.findEligibleUsers(query);
            request.setAttribute("users", eligibleUsers);
        } else if ("places".equals(tab)) {
            List<Place> places = placeService.findEligiblePlaces(query);
            request.setAttribute("places", places);
        }

        request.getRequestDispatcher("/WEB-INF/views/search-page.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
