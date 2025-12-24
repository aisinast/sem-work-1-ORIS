package ru.itis.spotty.repositories;

import ru.itis.spotty.models.Place;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

public class JdbcPlaceRepository implements PlaceRepository {

    private final String CREATE_PLACES_TABLE_QUERY = """
                CREATE TABLE IF NOT EXISTS places (
                    place_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                    place_name VARCHAR(120) NOT NULL,
                    full_address VARCHAR(250) NOT NULL
                );
    """;

    private final String GET_PLACE_BY_ID_QUERY = """
                SELECT * FROM places WHERE place_id = ?;
    """;

    private final String CREATE_NEW_PLACE_QUERY = """
                INSERT INTO places (place_name, full_address)
                VALUES (?, ?)
                RETURNING place_id;
    """;

    private final String FIND_ELIGIBLE_PLACES_QUERY = """
                SELECT *
                FROM places
                WHERE full_address ILIKE ?
                    OR place_name ILIKE ?;
    """;

    private Properties properties;
    private String url;

    public JdbcPlaceRepository() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        properties = new Properties();

        InputStream in = getClass().getClassLoader().getResourceAsStream("/application.properties");
        try {
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        url = properties.getProperty("url");

        try (Connection connection = DriverManager.getConnection(url, properties);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_PLACES_TABLE_QUERY);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UUID createPlace(Place place) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement preparedStatement = connection.prepareStatement(CREATE_NEW_PLACE_QUERY)) {
            preparedStatement.setString(1, place.getPlaceName());
            preparedStatement.setString(2, place.getFullAddress());

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                UUID placeId = UUID.fromString(resultSet.getString("place_id"));
                place.setPlaceId(placeId);
                resultSet.close();
                return placeId;
            }

            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public Place getPlaceById(UUID placeId) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement preparedStatement = connection.prepareStatement(GET_PLACE_BY_ID_QUERY)) {
            preparedStatement.setObject(1, placeId, Types.OTHER);

            ResultSet resultSet = preparedStatement.executeQuery();
            Place place = null;
            if (resultSet.next()) {
                place = toPlace(resultSet);
            }

            resultSet.close();
            return place;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Place> findEligiblePlaces(String query) {
        List<Place> places = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ELIGIBLE_PLACES_QUERY)) {
            String preparedQuery = "%" + query.trim()  + "%";
            preparedStatement.setString(1, preparedQuery);
            preparedStatement.setString(2, preparedQuery);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Place place = toPlace(resultSet);
                places.add(place);
            }

            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return places;
    }

    private Place toPlace(ResultSet rs) throws SQLException {
        UUID placeId = UUID.fromString(rs.getString("place_id"));
        String placeName = rs.getString("place_name");
        String fullAddress = rs.getString("full_address");

        return new Place(placeId, placeName, fullAddress);
    }
}
