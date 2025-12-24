package ru.itis.spotty.repositories;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import java.util.UUID;

public class JdbcFollowingRepository implements FollowingRepository {

    private final String CREATE_FOLLOWINGS_TABLE_QUERY = """
            CREATE TABLE IF NOT EXISTS followings (
                follower_id UUID NOT NULL,
                followed_to_id UUID NOT NULL,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            );
    """;

    private final String COUNT_FOLLOWERS_QUERY = """
            SELECT COUNT(*) FROM followings WHERE followed_to_id = ?;
    """;

    private final String COUNT_FOLLOWED_TO_QUERY = """
            SELECT COUNT(*) FROM followings WHERE follower_id = ?;
    """;

    private final String FOLLOW_QUERY = """
            INSERT INTO followings (follower_id, followed_to_id)
            VALUES (?, ?);
    """;

    private final String UNFOLLOW_QUERY = """
            DELETE FROM followings
            WHERE follower_id = ?
                AND followed_to_id = ?;
    """;

    private final String IS_FOLLOWING_QUERY = """
            SELECT
                COUNT(*)
            FROM followings
            WHERE follower_id = ?
                AND followed_to_id = ?;
    """;

    private Properties properties;
    private String url;

    public JdbcFollowingRepository() {
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

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_FOLLOWINGS_TABLE_QUERY);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int countFollowers(UUID userId) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement preparedStatement = connection.prepareStatement(COUNT_FOLLOWERS_QUERY)) {
            preparedStatement.setObject(1, userId, Types.OTHER);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                resultSet.close();
                return count;
            }

            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return 0;
    }

    @Override
    public int countFollowedToId(UUID userId) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement preparedStatement = connection.prepareStatement(COUNT_FOLLOWED_TO_QUERY)) {
            preparedStatement.setObject(1, userId, Types.OTHER);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                resultSet.close();
                return count;
            }

            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return 0;
    }

    @Override
    public void follow(UUID userId, UUID followedToId) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement preparedStatement = connection.prepareStatement(FOLLOW_QUERY)) {
            preparedStatement.setObject(1, userId, Types.OTHER);
            preparedStatement.setObject(2, followedToId, Types.OTHER);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unfollow(UUID userId, UUID followedToId) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement preparedStatement = connection.prepareStatement(UNFOLLOW_QUERY)) {
            preparedStatement.setObject(1, userId, Types.OTHER);
            preparedStatement.setObject(2, followedToId, Types.OTHER);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isFollowing(UUID userId, UUID followedToId) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement preparedStatement = connection.prepareStatement(IS_FOLLOWING_QUERY)) {
            preparedStatement.setObject(1, userId, Types.OTHER);
            preparedStatement.setObject(2, followedToId, Types.OTHER);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                resultSet.close();
                return count > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
