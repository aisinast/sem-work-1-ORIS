package ru.itis.spotty.repositories;

import ru.itis.spotty.models.Post;
import ru.itis.spotty.models.Tag;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class JdbcPostTagRepository implements PostTagRepository {
    private final String CREATE_POST_TAGS_TABLE_QUERY = """
                CREATE TABLE IF NOT EXISTS post_tags (
                    tag_id UUID,
                    post_id UUID
                );
    """;

    private final String GET_TAGS_BY_POST_ID_QUERY = """
                SELECT
                    pt.tag_id,
                    t.tag_name,
                    t.category
                from post_tags pt
                LEFT JOIN tags t
                    on t.tag_id = pt.tag_id
                WHERE pt.post_id = ?
    """;

    private final String ADD_POST_TAG_QUERY = """
            INSERT INTO post_tags (post_id, tag_id)
            VALUES (?, ?)
    """;

    private final String DELETE_ALL_POST_TAGS_QUERY = """
            DELETE FROM post_tags
            WHERE post_id = ?;
    """;

    private Properties properties;
    private String url;

    public JdbcPostTagRepository() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        properties = new Properties();

        InputStream input = getClass().getClassLoader().getResourceAsStream("/application.properties");
        try {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        url = properties.getProperty("url");

        try (Connection connection = DriverManager.getConnection(url, properties);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_POST_TAGS_TABLE_QUERY);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Tag> getPostTags(UUID post_id) {
        ArrayList<Tag> tags = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement preparedStatement = connection.prepareStatement(GET_TAGS_BY_POST_ID_QUERY)) {
            preparedStatement.setObject(1, post_id, Types.OTHER);

            ResultSet resultSet = preparedStatement.executeQuery();

            Tag tag;
            while (resultSet.next()) {
                tag = toTag(resultSet);
                tags.add(tag);
            }

            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return tags;
    }

    @Override
    public void addPostTag(UUID post_id, UUID tag_id) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement preparedStatement = connection.prepareStatement(ADD_POST_TAG_QUERY)) {
            preparedStatement.setObject(1, post_id, Types.OTHER);
            preparedStatement.setObject(2, tag_id, Types.OTHER);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAllPostTags(UUID post_id) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ALL_POST_TAGS_QUERY)) {
            preparedStatement.setObject(1, post_id, Types.OTHER);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Tag toTag(ResultSet rs) throws SQLException {
        UUID tag_id = rs.getObject("tag_id", UUID.class);
        String tag_name = rs.getString("tag_name");
        String category = rs.getString("category");

        Tag tag = new Tag(tag_id, tag_name, category);
        return tag;
    }

    private Post toPost(ResultSet rs) throws SQLException {
        UUID post_id = rs.getObject("post_id", UUID.class);
        String title = rs.getString("title");
        String textContent = rs.getString("text_content");
        UUID authorId = rs.getObject("author_id", UUID.class);
        UUID placeId = rs.getObject("place_id", UUID.class);
        String imageUrl = rs.getString("image_url");
        LocalDateTime createdAt = rs.getObject("created_at", LocalDateTime.class);

        Post post = new Post(post_id, title, textContent, authorId, placeId, imageUrl,  createdAt);
        return post;
    }
}
