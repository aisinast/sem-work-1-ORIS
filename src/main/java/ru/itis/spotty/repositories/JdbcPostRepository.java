package ru.itis.spotty.repositories;

import ru.itis.spotty.models.Place;
import ru.itis.spotty.models.Post;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class JdbcPostRepository implements PostRepository {

    private final String CREATE_POSTS_TABLE_QUERY = """
            CREATE TABLE IF NOT EXISTS posts (
                post_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                title VARCHAR(120) NOT NULL,
                text_content VARCHAR(2000),
                authorId UUID NOT NULL,
                place_id UUID NOT NULL,
                image_url VARCHAR(2000) NOT NULL,
                created_at TIMESTAMP default CURRENT_TIMESTAMP
            );
    """;

    private final String ADD_NEW_POST_QUERY = """
            INSERT INTO posts (title, text_content, authorId, place_id, image_url) 
            VALUES (?, ?, ?, ?, ?)
            RETURNING post_id;
    """;

    private final String UPDATE_POST_QUERY = """
            UPDATE posts
            SET
                title = ?,
                text_content = ?,
                place_id = ?,
                image_url = ?
            WHERE post_id = ?;
    """;

    private final String DELETE_POST_QUERY = """
            DELETE FROM posts WHERE post_id = ?;
    """;

    private final String GET_POST_BY_ID_QUERY = """
            SELECT * FROM posts WHERE post_id = ?;
    """;

    private final String GET_POSTS_BY_USER_ID_QUERY = """
            SELECT * FROM posts
            WHERE authorId = ?
            ORDER BY created_at DESC;
    """;

    private final String COUNT_POSTS_BY_USER_ID_QUERY = """
            SELECT COUNT(*) FROM posts
            WHERE authorId = ?;
    """;

    private final String GET_POSTS_BY_PLACE_ID_QUERY = """
            SELECT * FROM posts
            WHERE place_id = ?
            ORDER BY created_at DESC;
    """;

    private final String GET_POSTS_FOR_POST_PAGE_QUERY = """
            SELECT * FROM posts
            ORDER BY created_at DESC
            LIMIT ?
            OFFSET ?
    """;

    private final String GET_TOTAL_POSTS_COUNT_QUERY = """
            SELECT COUNT(*) FROM posts;
    """;

    private Properties properties;
    private String url;

    public JdbcPostRepository() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        properties = new Properties();

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("/application.properties");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        url = properties.getProperty("url");

        try (Connection connection = DriverManager.getConnection(url, properties);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_POSTS_TABLE_QUERY);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public UUID addNewPost(Post post) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement preparedStatement = connection.prepareStatement(ADD_NEW_POST_QUERY)) {
            preparedStatement.setString(1, post.getTitle());
            preparedStatement.setString(2, post.getContent());
            preparedStatement.setObject(3, post.getAuthorId(), Types.OTHER);
            preparedStatement.setObject(4, post.getPlaceId(), Types.OTHER);
            preparedStatement.setObject(5, post.getImageUrl(), Types.OTHER);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                UUID postId = resultSet.getObject("post_id", UUID.class);
                resultSet.close();
                return postId;
            }

            resultSet.close();
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updatePost(Post post) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_POST_QUERY)) {
            preparedStatement.setString(1, post.getTitle());
            preparedStatement.setString(2, post.getContent());
            preparedStatement.setObject(3, post.getPlaceId(), Types.OTHER);
            preparedStatement.setObject(4, post.getImageUrl(), Types.OTHER);
            preparedStatement.setObject(5, post.getPostId(), Types.OTHER);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deletePost(Post post) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_POST_QUERY)) {
            preparedStatement.setObject(1, post.getPostId(), Types.OTHER);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Post getPostById(UUID id) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement preparedStatement = connection.prepareStatement(GET_POST_BY_ID_QUERY)) {
            preparedStatement.setObject(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            Post post;
            if (resultSet.next()) {
                post = toPost(resultSet);
                resultSet.close();
                return post;
            }

            resultSet.close();
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Post> getPostsByUserId(UUID id) {
        List<Post> posts = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement preparedStatement = connection.prepareStatement(GET_POSTS_BY_USER_ID_QUERY)) {
            preparedStatement.setObject(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            Post post;
            while (resultSet.next()) {
                post = toPost(resultSet);
                posts.add(post);
            }

            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return posts;
    }

    @Override
    public int getUserPostsCount(UUID id) {
        int count = 0;

        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement preparedStatement = connection.prepareStatement(COUNT_POSTS_BY_USER_ID_QUERY)) {
            preparedStatement.setObject(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if  (resultSet.next()) {
                count = resultSet.getInt("count");
            }

            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return count;
    }

    @Override
    public List<Post> getPostsByPlaceId(UUID placeId) {
        List<Post> posts = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement preparedStatement = connection.prepareStatement(GET_POSTS_BY_PLACE_ID_QUERY)) {
            preparedStatement.setObject(1, placeId);

            ResultSet resultSet = preparedStatement.executeQuery();

            Post post;
            while (resultSet.next()) {
                post = toPost(resultSet);
                posts.add(post);
            }

            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return posts;
    }

    @Override
    public List<Post> getPostsForPostPage(int limit, int offset) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement statement = connection.prepareStatement(GET_POSTS_FOR_POST_PAGE_QUERY)) {

            statement.setInt(1, limit);
            statement.setInt(2, offset);

            List<Post> posts = new ArrayList<>();

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    posts.add(toPost(resultSet));
                }
            }

            return posts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getTotalPostsCount() {
        int count = 0;

        try (Connection connection = DriverManager.getConnection(url, properties);
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(GET_TOTAL_POSTS_COUNT_QUERY);
            if (resultSet.next()) {
                count = resultSet.getInt("count");
            }

            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return count;
    }

    private Post toPost(ResultSet rs) throws SQLException {
        UUID postId = (UUID) rs.getObject("post_id");
        String title = rs.getString("title");
        String textContent = rs.getString("text_content");
        UUID authorId = (UUID) rs.getObject("authorId");
        UUID placeId = (UUID) rs.getObject("place_id");
        String imageUrl = rs.getString("image_url");
        LocalDateTime createdAt =  rs.getTimestamp("created_at").toLocalDateTime();

        Post post = new Post(postId, title, textContent, authorId, placeId, imageUrl, createdAt);

        return post;
    }
}
