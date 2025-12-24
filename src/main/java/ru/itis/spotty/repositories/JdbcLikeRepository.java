package ru.itis.spotty.repositories;

import ru.itis.spotty.models.Post;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class JdbcLikeRepository implements LikeRepository {

    private final String CREATE_LIKES_TABLE_QUERY = """
            CREATE TABLE IF NOT EXISTS likes (
                post_id UUID NOT NULL,
                user_id UUID NOT NULL
            );
    """;

    private final String ADD_NEW_LIKE_QUERY = """
            INSERT INTO likes (user_id, post_id)
            VALUES (?, ?);
    """;

    private final String DELETE_LIKE_QUERY = """
            DELETE FROM likes
            WHERE post_id = ?
                AND user_id = ?;
    """;

    private final String COUNT_POST_LIKES_QUERY = """
            SELECT COUNT(*)
            FROM likes
            WHERE post_id = ?;
    """;

    private final String IS_USER_LIKED_QUERY = """
            SELECT COUNT(*) FROM likes
            WHERE user_id = ?
                AND post_id = ?;
    """;

    private final String GET_USER_LIKED_POSTS_QUERY = """
            SELECT
                p.*
            FROM likes l
            LEFT JOIN posts p
                ON l.post_id = p.post_id
            WHERE l.user_id = ?;
    """;

    private String url;
    private Properties properties;

    public JdbcLikeRepository() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        properties = new Properties();

        InputStream in = JdbcLikeRepository.class.getClassLoader().getResourceAsStream("/application.properties");
        try {
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        url = properties.getProperty("url");

        try (Connection connection = DriverManager.getConnection(url, properties);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_LIKES_TABLE_QUERY);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addLike(UUID postId, UUID userId) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement preparedStatement = connection.prepareStatement(ADD_NEW_LIKE_QUERY)) {
            preparedStatement.setObject(1, userId);
            preparedStatement.setObject(2, postId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeLike(UUID postId, UUID userId) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_LIKE_QUERY)) {
            preparedStatement.setObject(1, postId);
            preparedStatement.setObject(2, userId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int countPostLikes(UUID postId) {
        int count = 0;

        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement preparedStatement = connection.prepareStatement(COUNT_POST_LIKES_QUERY)) {
            preparedStatement.setObject(1, postId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }

            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return count;
    }

    @Override
    public boolean isCurrentUserLiked(UUID postId, UUID userId) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement preparedStatement = connection.prepareStatement(IS_USER_LIKED_QUERY)) {
            preparedStatement.setObject(1, userId);
            preparedStatement.setObject(2, postId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }

            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    @Override
    public List<Post> getLikedPosts(UUID userId) {
        List<Post> posts = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_LIKED_POSTS_QUERY)) {
            preparedStatement.setObject(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Post post = toPost(resultSet);
                posts.add(post);
            }

            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return posts;
    }

    private Post toPost(ResultSet rs) throws SQLException {
        UUID postId = UUID.fromString(rs.getString("post_id"));
        String title = rs.getString("title");
        String content = rs.getString("text_content");
        UUID authorId = UUID.fromString(rs.getString("authorId"));
        UUID placeId = UUID.fromString(rs.getString("place_id"));
        String imageUrl = rs.getString("image_url");
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();

        return new Post(postId, title, content, authorId, placeId, imageUrl, createdAt);
    }
}
